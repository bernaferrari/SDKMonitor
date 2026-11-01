"""Capture SDK Monitor's store screens on the capture emulator (needs adb, Node and Pillow).

`npx screenshot-studio recapture` builds and installs the capture build (studio.json → capture),
runs this tour for every device size and exports. The tour installs the fictional apps of
fixture-apps.json (code-free APKs, each with a real targetSdk), and the capture build lists only those and
seeds their earlier versions, so the list, change log and details read like a phone tracked for a year.
Taps are recorded in en-US/raw/tap-map.json, so other locales replay them in place.
Run alone: npx screenshot-studio tour [--profile phone] [--locale de-DE].
The emulator plumbing is the studio's capture.py.
"""
import os
import subprocess
import time
from pathlib import Path

from capture import Device, parser

listing = Path(__file__).resolve().parent

args = parser(__doc__).parse_args()
d = Device(args, 'com.bernaferrari.sdkmonitor.storecapture', 'com.bernaferrari.sdkmonitor.MainActivity')
# recapture sets SCREENSHOT_STUDIO (and PLAYWRIGHT_MODULE, which draws the fixture icons).
fixtures = ['node', os.environ['SCREENSHOT_STUDIO'], 'fixtures', str(listing / 'fixture-apps.json')]


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
    subprocess.run([*fixtures, '--install', d.serial], check=True)
    for profile in d.profiles():
        d.night(False)
        launch()
        shot('01-apps')
        tap('Clearsky Weather')
        shot('02-details')
        tap('Logs', text_only=True)
        shot('05-insights')  # the tab opens on SDK analytics
        tap('Android 16')  # a legend tile opens the apps behind it, in a bottom sheet
        shot('06-sdk-sheet')
        d.back()
        time.sleep(1.5)  # let the sheet finish closing, or the scroll below lands on it
        if d.width > d.height:  # the landscape tablet shows details beside the log
            # The log is the narrow left pane; scroll there, not over the empty detail pane.
            for _ in range(6):
                if d.present('Harbor Chat'):
                    break
                d.swipe(int(d.width * .2), int(d.height * .8), int(d.width * .2), int(d.height * .35))
            tap('Harbor Chat')
        else:
            d.scroll(.5)  # then the change log under it
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
    subprocess.run([*fixtures, '--uninstall', d.serial], check=False)
