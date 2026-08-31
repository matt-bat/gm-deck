import { mkdir } from 'node:fs/promises';
import { pathToFileURL } from 'node:url';
import path from 'node:path';

const moduleName = process.env.GMDECK_PLAYWRIGHT_MODULE || 'playwright';
const { chromium } = await import(moduleName);
const root = path.resolve(import.meta.dirname, '..');
const screenshotDir = path.join(root, 'screenshots');
await mkdir(screenshotDir, { recursive: true });

const launchOptions = { headless: true };
if (process.env.GMDECK_CHROMIUM) launchOptions.executablePath = process.env.GMDECK_CHROMIUM;
const browser = await chromium.launch(launchOptions);
const context = await browser.newContext({ viewport: { width: 960, height: 480 }, deviceScaleFactor: 1 });
const page = await context.newPage();
const errors = [];
page.on('pageerror', error => errors.push(error.message));
await page.goto(pathToFileURL(path.join(root, 'assets', 'index.html')).href);
await page.evaluate(() => localStorage.clear());
await page.reload();

const check = (condition, message) => {
  if (!condition) throw new Error(message);
};
const click = selector => page.locator(selector).click();
const assertChromeFits = async label => {
  const result = await page.evaluate(() => {
    const viewport = { width: innerWidth, height: innerHeight };
    const selectors = ['header', 'header nav', '#status', '#controlStrip'];
    const boxes = selectors.map(selector => {
      const rect = document.querySelector(selector).getBoundingClientRect();
      return { selector, left: rect.left, top: rect.top, right: rect.right, bottom: rect.bottom };
    });
    const footerChildrenFit = [...document.querySelectorAll('#controlStrip > *')]
      .filter(element => getComputedStyle(element).display !== 'none')
      .every(element => {
        const rect = element.getBoundingClientRect();
        return rect.left >= -1 && rect.right <= innerWidth + 1 && rect.top >= -1 && rect.bottom <= innerHeight + 1;
      });
    return {
      viewport,
      boxes,
      footerChildrenFit,
      documentWidth: document.documentElement.scrollWidth,
      bodyWidth: document.body.scrollWidth
    };
  });
  check(result.viewport.width === 960 && result.viewport.height === 480, `${label}: wrong viewport`);
  check(result.documentWidth <= 960 && result.bodyWidth <= 960, `${label}: horizontal page overflow`);
  check(result.footerChildrenFit, `${label}: footer child is clipped or off-screen`);
  for (const box of result.boxes) {
    check(box.left >= -1 && box.top >= -1 && box.right <= 961 && box.bottom <= 481, `${label}: ${box.selector} exceeds viewport`);
  }
};

await click('#welcomeDone');
await assertChromeFits('DM view');
await page.screenshot({ path: path.join(screenshotDir, 'gm-deck-3.1-dm-view.png') });

await click('#viewSwitch');
check(await page.locator('#viewPanel').isVisible(), 'Role chooser did not open on the first tap');
await page.screenshot({ path: path.join(screenshotDir, 'gm-deck-3.1-role-switcher.png') });
await click('[data-view-choice="player"]');
check(await page.locator('#player').isVisible(), 'Player view did not open on the second tap');
check(await page.locator('body').getAttribute('data-view') === 'player', 'Player role was not applied');
check(await page.locator('.player-condition').count() === 8, 'Player view must expose eight quick conditions');
check(await page.locator('[data-role="dm"]:visible').count() === 0, 'DM-only navigation remained visible in Player view');
check(!await page.locator('#sceneControl').isVisible() && !await page.locator('#sessionMode').isVisible(), 'DM-only footer controls remained visible in Player view');
await assertChromeFits('Player view');

await page.locator('#playerName').fill('Valeros');
await page.locator('#playerMaxHp').fill('30');
await page.locator('#playerMaxHp').dispatchEvent('change');
await click('[data-player-hp="-1"]');
await page.locator('#playerAttack').fill('7');
await page.locator('#playerAttack').dispatchEvent('change');
await click('[data-player-roll="attack"]');
const playerState = await page.evaluate(() => JSON.parse(localStorage.getItem('player:main')));
check(playerState.name === 'Valeros' && playerState.maxHp === 30 && playerState.hp === 29 && playerState.attack === 7, 'Player state did not persist per campaign');
check(/^\d+$/.test(await page.locator('#stripDice').textContent()), 'Player quick roll did not update the persistent dice result');
await page.screenshot({ path: path.join(screenshotDir, 'gm-deck-3.1-player-view.png') });

await click('#viewSwitch');
await click('[data-view-choice="dm"]');
await click('#sceneControl');
check(await page.locator('#scenePanel').isVisible(), 'Scene Director did not open');
check(await page.locator('[data-scene]').count() === 6, 'Scene Director must expose six presets');
await assertChromeFits('Scene Director');
await page.screenshot({ path: path.join(screenshotDir, 'gm-deck-3.1-scene-director.png') });
await click('[data-scene="break"]');
const sceneState = await page.evaluate(() => ({
  app: JSON.parse(localStorage.getItem('appState')),
  timer: JSON.parse(localStorage.getItem('timerEnd:main'))
}));
check(sceneState.app.campaigns.main.activeScene === 'break', 'Scene selection was not persisted');
check(sceneState.app.campaigns.main.mode === 'break', 'Scene mode was not applied');
check(sceneState.timer > Date.now(), 'Scene timer was not started');
check(await page.locator('#sounds').isVisible(), 'Scene destination did not open');

await click('#sceneControl');
await page.locator('#sceneEditor').evaluate(element => { element.open = true; });
await page.locator('#sceneEditSelect').selectOption('prep');
await page.locator('#sceneName').fill('Session Prep');
await click('#saveScene');
const editedName = await page.evaluate(() => JSON.parse(localStorage.getItem('appState')).campaigns.main.scenes.find(scene => scene.id === 'prep').name);
check(editedName === 'Session Prep', 'Edited scene name was not persisted');
await page.locator('#sessionMode').selectOption('combat');
await page.locator('#sessionMode').dispatchEvent('change');
const activeScene = await page.evaluate(() => JSON.parse(localStorage.getItem('appState')).campaigns.main.activeScene);
check(activeScene === null, 'Manual mode change did not clear the active scene');

check(errors.length === 0, `Browser errors: ${errors.join('; ')}`);
console.log(JSON.stringify({
  viewport: '960x480',
  roleSwitchTaps: 2,
  playerPersistence: 'passed',
  playerDice: 'passed',
  sceneApplyEditAndClear: 'passed',
  screenshots: 4,
  browserErrors: errors.length
}, null, 2));
await browser.close();
