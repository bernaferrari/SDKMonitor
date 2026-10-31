# SDK Monitor · Play listing

Built with [Screenshot Studio](studio/). Read `~/Documents/screenshot-studio/PLAYBOOK.md` first.

| Change | File |
| --- | --- |
| Cards, order, captures | `gallery.json` |
| Words per language | `<locale>/copy.json`, `<locale>/listing.json` |
| Look | `theme.css`, `theme.js` |
| Canvas and capture sizes | `profiles.json` |
| Scene tour | `capture-emulator.py` (build and runs: `studio.json` → `capture`) |

```sh
node store-listing/studio/recapture.cjs          # build, capture on emulator-5578, export
node store-listing/studio/server.cjs 8790        # editor
node store-listing/studio/export.cjs --locale en-US
```

## README art and the web share image

Every image the README shows (`en-US/readme/*.jpg`) and the web demo's share image
(`en-US/social/opengraph-image.png`, 1280 x 640) are cards in `gallery.json`, built from the latest
captures, so they follow the app as it changes. After an export, copy the share image into the web demo:

```sh
cp store-listing/en-US/social/opengraph-image.png webApp/src/wasmJsMain/resources/opengraph-image.png
```
