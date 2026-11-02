// SDK Monitor's store screens on the capture emulator. The tour installs the fictional apps of fixture-apps.json
// (code-free APKs, each with a real targetSdk); the capture build lists only those and seeds their earlier versions, so
// the list, change log and details read like a phone tracked for a year. `npx screenshot-studio recapture` installs the
// capture build, runs this for every language and device size, and exports. Alone: npx screenshot-studio tour.

/** @type {import('screenshot-studio').Tour} */
export default {
  app: 'com.bernaferrari.sdkmonitor.storecapture',
  activity: 'com.bernaferrari.sdkmonitor.MainActivity',
  setup: d => d.studio('fixtures', 'fixture-apps.json', '--install', d.serial),
  teardown: d => d.studio('fixtures', 'fixture-apps.json', '--uninstall', d.serial),
  async start(d) {
    // Key events (Home, Back) leave touch mode, and then Compose focuses the search field and opens the keyboard.
    // A tap on the status bar puts the display back in touch mode.
    await d.tapAt(d.width / 2, 4);
    if (d.theme === 'dark') return d.launch();
    d.setClock('09:41');
    await d.launch({clear: true});
    try { d.shell('pm', 'grant', d.app, 'android.permission.POST_NOTIFICATIONS'); } catch {}
    await d.launch();
    await d.waitFor('Atlas Bank');  // the first start seeds the history, then lists the apps
  },
  scenes: {
    light: {
      '01-apps': null,
      '02-details': d => d.tap('Clearsky Weather'),
      '05-insights': d => d.tap('Logs', {textOnly: true}),  // the tab opens on SDK analytics
      '06-sdk-sheet': d => d.tap('Android 16'),             // a legend tile opens the apps behind it, in a sheet
      async '03-logs'(d) {
        await d.back();
        if (d.width < d.height) return d.scroll(.5);  // the change log, under the analytics
        // The landscape tablet shows details beside the log: scroll the narrow left pane to an app, and open it.
        for (let i = 0; i < 6 && !(await d.isVisible('Harbor Chat')); i++) await d.swipe(d.width * .2, d.height * .8, d.width * .2, d.height * .35);
        await d.tap('Harbor Chat');
      },
      '04-settings': d => d.tap('Settings', {textOnly: true}),
    },
    dark: {
      '11-apps-dark': d => d.tap('Apps', {textOnly: true}),
      '12-details-dark': d => d.tap('Atlas Bank'),
    },
  },
};
