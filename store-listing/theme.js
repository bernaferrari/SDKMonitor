// SDK Monitor: "strata". A dark ember field lit by the icon's bands: red rising from the lower left,
// orange across the middle, a yellow glow high on the right, laid out across the whole strip so every
// seam continues. Nothing is random, and nothing sits between the reader and the app.
import {panoramaGradient, panoramaLights} from './studio/shapes.js';


function art(ctx) {
  const {W, H} = ctx;
  const defs = panoramaGradient(ctx, 'field', ['#150b07', '#1b0e08', '#160c07', '#1e1009', '#150b07', '#1c0f08']);
  let body = `<rect width="${W}" height="${H}" fill="url(#field)"/>`;
  // The strata glow. On the Play strip it is laid out once across every card, so each seam continues
  // into the next; a README image (always seen alone) gets the whole glow to itself.
  const lights = [[.15, .95, .55, '#d8361f', .42], [.85, .18, .5, '#f5a300', .30], [.55, .7, .45, '#ef6a1f', .24],
                  [.95, .92, .32, '#e0452a', .3], [.05, .1, .4, '#6a2310', .5]].map(([x, y, r, color, opacity]) => ({x, y, r, color, opacity}));
  body += panoramaLights(ctx, lights, {id: 'strata', alone: ctx.kind === 'readme'});
  return `<defs>${defs}</defs>${body}`;
}

export default {
  art,
  continuous: true,

  compose({main}) {
    // A card without a headline (the README trio, under its own section heading) has no copy.
    const header = main.querySelector('header');
    if (header && !header.querySelector('h1')?.textContent.trim()) header.remove();
  },
};
