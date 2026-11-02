# SDK Monitor · Play listing

Built with [Screenshot Studio](https://github.com/bernaferrari/screenshot-studio) (`npx screenshot-studio`); the app keeps no copy of it.

| Change | File |
| --- | --- |
| Cards, order, captures | `gallery.json` |
| Words per language | `<locale>/copy.json`, `<locale>/listing.json` |
| Look | `theme.css`, `theme.js` |
| Canvas and capture sizes | `profiles.json` |
| Scene tour | `capture.tour.mjs` (build and runs: `studio.json` → `capture`) |

```sh
npx screenshot-studio recapture                 # build, capture on emulator-5578, export
npx screenshot-studio tour --profile phone       # the scene tour alone
npx screenshot-studio studio                    # editor
npx screenshot-studio export --locale en-US
npx screenshot-studio check                     # fast check after each edit
```

## README art and the web share image

Every image the README shows (`en-US/readme/*.jpg`) and the web demo's share image
(`en-US/social/opengraph-image.png`, 1280 x 640) are cards in `gallery.json`, built from the latest
captures, so they follow the app as it changes. After an export, copy the share image into the web demo:

```sh
cp store-listing/en-US/social/opengraph-image.png webApp/src/wasmJsMain/resources/opengraph-image.png
```
