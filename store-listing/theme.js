// SDK Monitor, after the original header: peach to sky blue with soft glows, a white curve, a frosted tile and
// fine dot grids, continuous across the Play strip. The share image adds the app's own idea, Android API levels
// as a line of seals, each in the shape and color the app gives that level.
import {panoramaGradient, panoramaLights, panoramaX} from './studio/shapes.js';

const LATEST = 37;
// The share image shows one seal of each shape, from far behind to current.
const ALONE = [33, 34, 35, 36, 37];
// The seal's edge: the same color, darker.
const edge = hex => '#' + hex.slice(1).match(/../g).map(c => Math.round(parseInt(c, 16) * .68).toString(16).padStart(2, '0')).join('');
const PEACH = '#fde0cf';
const PAPER = '#f5f2f3';
const SKY = '#d6e3fb';
const INK = '#161b26';

// The app's colors per level (ApiLevel.colorArgb) and its shapes per level (ApiLevelUi.apiShape).
const color = level => ({0: '#14B572', 1: '#178E96', 2: '#E37A46', 3: '#E54B4B'})[LATEST - level] ?? '#D31B33';

function polar(fn, cx, cy, r, rot = 0) {
  const points = [];
  for (let i = 0; i < 720; i++) {
    const t = i / 720 * Math.PI * 2;
    const k = r * fn(t - rot);
    points.push(`${(cx + k * Math.cos(t)).toFixed(1)},${(cy + k * Math.sin(t)).toFixed(1)}`);
  }
  return `M${points.join('L')}Z`;
}

/** A regular polygon with rounded corners (Material's pentagon, diamond, triangle). */
function rounded(n, cx, cy, r, rot, round = .22) {
  const pts = Array.from({length: n}, (_, i) => {
    const a = rot + i * 2 * Math.PI / n;
    return [cx + r * Math.cos(a), cy + r * Math.sin(a)];
  });
  let d = '';
  pts.forEach((p, i) => {
    const prev = pts[(i + n - 1) % n], next = pts[(i + 1) % n];
    const a = [p[0] + (prev[0] - p[0]) * round, p[1] + (prev[1] - p[1]) * round];
    const b = [p[0] + (next[0] - p[0]) * round, p[1] + (next[1] - p[1]) * round];
    d += `${i ? 'L' : 'M'}${a[0].toFixed(1)},${a[1].toFixed(1)}Q${p[0].toFixed(1)},${p[1].toFixed(1)} ${b[0].toFixed(1)},${b[1].toFixed(1)}`;
  });
  return d + 'Z';
}

function seal(level, cx, cy, r) {
  switch (LATEST - level) {
    case 0: return polar(t => .88 + .12 * Math.cos(8 * t), cx, cy, r);            // sunny
    case 1: return polar(t => .93 + .07 * Math.cos(9 * t), cx, cy, r, -Math.PI / 2); // cookie
    case 2: return rounded(5, cx, cy + r * .06, r * 1.02, -Math.PI / 2);            // pentagon
    case 3: return rounded(4, cx, cy, r * 1.06, -Math.PI / 2, .2);                  // diamond
    default: return rounded(3, cx, cy + r * .2, r * 1.15, -Math.PI / 2, .24);       // triangle
  }
}

function art(ctx) {
  const {W, H, kind, slides} = ctx;
  const alone = kind === 'readme' || kind === 'social';
  const layout = ctx.slide?.layout;
  // The original header's field: peach in the top left, a pale middle, clear sky blue on the right, with soft
  // glows, a wide white curve and a frosted tile (fine dot grids on the Play strip only). On the Play strip it is one picture laid across
  // every card (x in card widths), the tiles and grids on the seams, half on each card.
  const wash = alone
    ? `<linearGradient id="wash" x1="0" y1="0" x2="1" y2=".6"><stop offset="0" stop-color="${PEACH}"/><stop offset=".45" stop-color="${PAPER}"/><stop offset="1" stop-color="${SKY}"/></linearGradient>`
    : panoramaGradient(ctx, 'wash', [PEACH, PAPER, SKY, PAPER, PEACH, SKY]);
  let body = `<defs>${wash}</defs><rect width="${W}" height="${H}" fill="url(#wash)"/>`;
  const u = Math.max(W, H), g = Math.min(W, H) * .02;
  const dots = (x0, y0, cols, rows) => {
    let d = '';
    for (let i = 0; i < cols; i++) for (let j = 0; j < rows; j++) d += `<circle cx="${x0 + i * g}" cy="${y0 + j * g}" r="${g * .12}"/>`;
    return `<g fill="#8a93a8" fill-opacity=".35">${d}</g>`;
  };
  const tile = (x, y, t, rot) =>
    `<rect x="${x - t / 2}" y="${y - t / 2}" width="${t}" height="${t}" rx="${t * .22}" fill="#ffffff" fill-opacity=".35" stroke="#ffffff" stroke-opacity=".8" stroke-width="${t * .01}" transform="rotate(${rot} ${x} ${y})"/>`;
  const curve = (cx, cy, r) =>
    `<circle cx="${cx}" cy="${cy}" r="${r}" fill="#ffffff" fill-opacity=".28"/><circle cx="${cx}" cy="${cy}" r="${r * 1.07}" fill="none" stroke="#ffffff" stroke-opacity=".6" stroke-width="${u * .003}"/>`;
  if (alone) {
    body += panoramaLights(ctx, [{x: .05, y: .85, r: .5, color: '#ffd0b8', opacity: .55}, {x: .85, y: .2, r: .55, color: '#c9dafb', opacity: .6}], {id: 'glow', alone});
    body += curve(-u * .1, H + u * .4, u * .55);
    const t = Math.min(W, H) * .22;
    body += tile(W + t * .1, H * .66, t, -6);
  } else {
    const n = slides.length, X = cards => panoramaX(ctx, cards);
    const lights = Array.from({length: n + 1}, (_, k) => ({x: k + (k % 2 ? .75 : .25), y: k % 2 ? .85 : .2, r: .6, color: k % 2 ? '#c9dafb' : '#ffd0b8', opacity: .55}));
    body += panoramaLights(ctx, lights, {id: 'glow', each: false});
    body += curve(X(n * .3), H * 1.55, W * 1.9);
    body += curve(X(n * .78), H * 1.7, W * 1.6);
    const t = Math.min(W, H) * .16;
    for (let k = 1; k < n; k += 2) body += tile(X(k), H * (k % 4 === 1 ? .62 : .72), t, k % 4 === 1 ? -8 : 10);
    for (let k = 2; k < n; k += 2) body += dots(X(k) - 2 * g, H * .08, 5, 4);
  }
  // The share image and the README header carry the line of seals at the bottom left, one per level, spanning the
  // name card above it (share image: icon edge to the tagline's end; header: under the name).
  if (layout !== 'banner') return body;
  const social = kind === 'social';
  const levels = ALONE, y = H * .8, r = H * (social ? .062 : .052), x0 = (social ? 80 : 150) + r, x1 = (social ? 690 : 1300) - r;
  body += `<defs><linearGradient id="sealLight" x1="0" x2="0" y1="0" y2="1"><stop offset="0" stop-color="#fff" stop-opacity=".28"/><stop offset=".55" stop-color="#fff" stop-opacity="0"/></linearGradient></defs>`;
  body += `<line x1="${x0}" x2="${x1}" y1="${y}" y2="${y}" stroke="${INK}" stroke-opacity=".1" stroke-width="4" stroke-dasharray="2 16" stroke-linecap="round"/>`;
  levels.forEach((level, i) => {
    const cx = x0 + i * (x1 - x0) / (levels.length - 1);
    const shape = seal(level, cx, y, r);
    const ty = y + (LATEST - level >= 4 ? r * .18 : 0) + r * .34;
    // Solid tokens: a soft shadow, the darker edge showing below (the seal's thickness), the face, light from above.
    const depth = r * .14;
    body += `<path d="${seal(level, cx, y + depth * 1.7, r)}" fill="#2a3040" fill-opacity=".14"/>`;
    body += `<path d="${seal(level, cx, y + depth, r)}" fill="${edge(color(level))}"/>`;
    body += `<path d="${shape}" fill="${color(level)}"/>`;
    body += `<path d="${shape}" fill="url(#sealLight)"/>`;
    body += `<text x="${cx}" y="${ty}" text-anchor="middle" font-family="Studio" font-weight="760" font-size="${r * .86}" letter-spacing="-.02em" fill="#fff">${level}</text>`;
  });
  return body;
}

export default {
  art,
  continuous: true,

  compose({main}) {
    // A card without a headline (the README trio, under its own section heading) has no copy.
    const header = main.querySelector('header');
    if (header && !header.querySelector('h1')?.textContent.trim()) header.remove();
  },

  // After the studio has sized every frame: each device is drawn as a real 3D device (phone3d.js).
  async layout({main}) {
    const devices = [...main.querySelectorAll('.device.device-frame-phone, .device.device-frame-tablet')];
    if (!devices.length) return;
    const {renderDevice} = await import('./phone3d.js');
    for (const device of devices) await renderDevice(main, device);
  },
};
