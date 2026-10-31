"""Capture SDK Monitor's store screens on the capture emulator (needs adb, Node and Pillow).

`node store-listing/studio/recapture.cjs` builds and installs the capture build (studio.json → capture),
runs this tour for every device size and exports. The tour installs the fictional apps of
fixture-apps.json (code-free APKs, each with a real targetSdk), and the capture build lists only those and
seeds their earlier versions, so the list, change log and details read like a phone tracked for a year.
Run alone: python3 store-listing/capture-emulator.py [--profile phone] [--locale de-DE].
The emulator plumbing is the studio's capture.py.
"""
import os
import subprocess
import sys
from pathlib import Path

listing = Path(__file__).resolve().parent
sys.path.insert(0, str(listing / 'studio'))
from capture import Device, parser  # noqa: E402

args = parser(__doc__).parse_args()
d = Device(args, 'com.bernaferrari.sdkmonitor.storecapture', 'com.bernaferrari.sdkmonitor.MainActivity')
fixtures = ['node', str(listing / 'studio/fixture-apps.cjs'), str(listing / 'fixture-apps.json')]
# Playwright draws the fixture icons; the studio master's copy serves when the app has none.
env = {'PLAYWRIGHT_MODULE': str(Path.home() / 'Documents/screenshot-studio/node_modules/playwright'), **os.environ}


def tap(label, wait=2.2, text_only=False):
    d.tap(label, wait, text_only=text_only, attempts=5)


def touch_mode():
    """Key events (Home, Back) leave touch mode, and then Compose focuses the search field and the
    keyboard opens. A tap on the status bar puts the display back in touch mode before a launch."""
    d.shell('input', 'tap', d.width // 2, 4)


def launch():
    d.set_clock('09:41')
    touch_mode()
    d.launch(clear=True, wait=0)
    d.shell('pm', 'grant', d.package, 'android.permission.POST_NOTIFICATIONS', check=False)
    d.launch(wait=6)  # the first start seeds the history, then lists the apps


def shot(name):
    d.shot(name, settle=1.5)


with d:
    subprocess.run([*fixtures, '--install', d.serial], check=True, env=env)
    for profile in d.profiles():
        d.night(False)
        launch()
        shot('01-apps')
        tap('Clearsky Weather')
        shot('02-details')
        if not d.present('Logs'):  # phones open details full screen; tablets show them beside the list
            d.back()
        tap('Logs', text_only=True)
        if d.present('Select an app to get started'):  # a wide screen shows details beside the log
            tap('Harbor Chat')
        shot('03-logs')
        tap('Settings', text_only=True)
        shot('04-settings')

        d.night(True)
        touch_mode()
        d.launch(wait=5)
        tap('Apps', text_only=True)
        shot('11-apps-dark')
        tap('Atlas Bank')
        shot('12-details-dark')
    subprocess.run([*fixtures, '--uninstall', d.serial], check=False, env=env)
