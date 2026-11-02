// SDK Monitor's store screens on the capture emulator. The tour installs the fictional apps of fixture-apps.json
// (code-free APKs, each with a real targetSdk); the capture build lists only those and seeds their earlier versions, so
// the list, change log and details read like a phone tracked for a year. `npx screenshot-studio recapture` installs the
// capture build, runs this for every language and device size, and exports. Alone: npx screenshot-studio tour.

const tap = (d, label, options = {}) => d.tap(label, {wait: 2200, attempts: 5, ...options});
const tryShell = (d, ...words) => { try { d.shell(...words); } catch {} };

/**
 * Key events (Home, Back) leave touch mode, and then Compose focuses the search field and the keyboard opens. A tap
 * on the status bar puts the display back in touch mode before a launch.
 */
const touchMode = d => d.shell('input', 'tap', Math.floor(d.width / 2), 4);

/** @type {import('screenshot-studio').Tour} */
export default {
  app: 'com.bernaferrari.sdkmonitor.storecapture',
  activity: 'com.bernaferrari.sdkmonitor.MainActivity',
  settle: 1500,
  setup: d => d.studio('fixtures', 'fixture-apps.json', '--install', d.serial),
  teardown: d => { try { d.studio('fixtures', 'fixture-apps.json', '--uninstall', d.serial); } catch {} },
  async start(d) {
    touchMode(d);
    if (d.theme === 'dark') return d.launch({wait: 5000});
    d.setClock('09:41');
    await d.launch({clear: true, wait: 0});
    tryShell(d, 'pm', 'grant', d.app, 'android.permission.POST_NOTIFICATIONS');
    await d.launch({wait: 6000});  // the first start seeds the history, then lists the apps
  },
  scenes: {
    light: {
      '01-apps': null,
      '02-details': d => tap(d, 'Clearsky Weather'),
      '05-insights': d => tap(d, 'Logs', {textOnly: true}),  // the tab opens on SDK analytics
      '06-sdk-sheet': d => tap(d, 'Android 16'),  // a legend tile opens the apps behind it, in a bottom sheet
      async '03-logs'(d) {
        await d.back();
        await d.sleep(1500);  // let the sheet finish closing, or the scroll below lands on it
        if (d.width < d.height) return d.scroll(.5);  // the change log under the analytics
        // The landscape tablet shows details beside the log: scroll the narrow left pane, not the empty detail pane.
        for (let i = 0; i < 6 && !(await d.present('Harbor Chat')); i++) await d.swipe(d.width * .2, d.height * .8, d.width * .2, d.height * .35);
        await tap(d, 'Harbor Chat');
      },
      '04-settings': d => tap(d, 'Settings', {textOnly: true}),
    },
    dark: {
      '11-apps-dark': d => tap(d, 'Apps', {textOnly: true}),
      '12-details-dark': d => tap(d, 'Atlas Bank'),
    },
  },
};
