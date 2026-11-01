// Every device on every card is a real 3D device, rendered with three.js (vendor/three, r180, MIT): a white body
// at true depth (a Galaxy S25 is 7.2 mm deep on 70.5 mm wide; a tablet about 6 mm on 165 mm) with softly rounded
// edges, a white glass face and the capture on its screen, posed exactly where the frame's CSS puts it (flat, or
// tilted by its transform). The DOM frame stays in place, hidden, so the studio's layout and bounds checks still
// see it; a canvas over the card draws the device instead.
import * as THREE from './vendor/three/three.module.min.js';
import {RoomEnvironment} from './vendor/three/RoomEnvironment.js';

const roundedRect = (w, h, r) => {
  const s = new THREE.Shape(), x = -w / 2, y = -h / 2;
  s.moveTo(x + r, y);
  s.lineTo(x + w - r, y); s.quadraticCurveTo(x + w, y, x + w, y + r);
  s.lineTo(x + w, y + h - r); s.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
  s.lineTo(x + r, y + h); s.quadraticCurveTo(x, y + h, x, y + h - r);
  s.lineTo(x, y + r); s.quadraticCurveTo(x, y, x + r, y);
  return s;
};

const DEPTH = {phone: 7.2 / 70.5, tablet: 6.1 / 165};

/** Draws `front` (a .device) as a 3D device on a canvas over the card `main`; resolves to that canvas once drawn. */
export async function renderDevice(main, front) {
  const style = getComputedStyle(front);
  // Card size in the frame's own (unzoomed) pixels: the share image zooms the frame, so it is drawn at the
  // header's size and the canvas zooms with it.
  const zoom = parseFloat(style.zoom) || 1;
  const cardW = main.clientWidth / zoom, cardH = main.clientHeight / zoom;
  const W = front.offsetWidth, H = front.offsetHeight;
  const bezel = parseFloat(style.paddingLeft), radius = parseFloat(style.borderTopLeftRadius);
  const cx = front.offsetLeft + W / 2, cy = front.offsetTop + H / 2;
  const short = Math.min(W, H), thickness = front.classList.contains('device-frame-tablet') ? DEPTH.tablet : DEPTH.phone;
  // The pose: the frame's CSS transform, split into its perspective distance and its rotation.
  const spec = [...document.styleSheets].flatMap(s => [...s.cssRules]).filter(r => r.selectorText && front.matches(r.selectorText) && r.style.transform).pop()?.style.transform || '';
  const perspective = parseFloat(/perspective\(([\d.]+)px\)/.exec(spec)?.[1] || 1600);
  const rotation = new DOMMatrix(spec.replace(/perspective\([^)]*\)/, '').trim() || 'none');

  const canvas = document.createElement('canvas');
  canvas.className = 'phone3d';
  Object.assign(canvas.style, {position: 'absolute', left: '0', top: '0', width: `${cardW}px`, height: `${cardH}px`, zoom: String(zoom), zIndex: style.zIndex === 'auto' ? 2 : style.zIndex, pointerEvents: 'none'});
  main.append(canvas);
  const renderer = new THREE.WebGLRenderer({canvas, alpha: true, antialias: true, preserveDrawingBuffer: true});
  renderer.setPixelRatio(window.devicePixelRatio * 1.5);
  renderer.setSize(cardW, cardH, false);
  renderer.outputColorSpace = THREE.SRGBColorSpace;
  renderer.toneMapping = THREE.NeutralToneMapping;

  const scene = new THREE.Scene();
  const pmrem = new THREE.PMREMGenerator(renderer);
  // Soft studio light from the room, a key light from the upper left in front, so the face is bright and the
  // edge turning away to the right a soft light gray, with a fill from the right so the white stays white.
  scene.environment = pmrem.fromScene(new RoomEnvironment(), .04).texture;
  scene.environmentIntensity = .95;
  const key = new THREE.DirectionalLight(0xffffff, 1.6);
  key.position.set(-1.4, 1.1, 1.3);
  scene.add(key);
  const fill = new THREE.DirectionalLight(0xffffff, .7);
  fill.position.set(1.5, .3, .6);
  scene.add(fill);
  renderer.toneMappingExposure = 1.12;

  // CSS perspective sits at the frame's centre, so the camera looks straight at it from `perspective` px away,
  // and a view offset moves that centre to where the frame is on the card. One unit is one CSS pixel.
  const camera = new THREE.PerspectiveCamera(2 * Math.atan(cardH / 2 / perspective) * 180 / Math.PI, cardW / cardH, 10, perspective * 4);
  camera.position.set(0, 0, perspective);
  camera.setViewOffset(cardW, cardH, cardW / 2 - cx, cardH / 2 - cy, cardW, cardH);

  const phone = new THREE.Group();
  // CSS's y axis points down, three's up: conjugate the rotation by a y flip.
  const m = rotation;
  phone.matrixAutoUpdate = false;
  phone.matrix.set(m.m11, -m.m21, m.m31, 0, -m.m12, m.m22, -m.m32, 0, m.m13, -m.m23, m.m33, 0, 0, 0, 0, 1);
  scene.add(phone);

  // The body: the frame's outline extruded backwards, its edge rounded by a bevel. Its front face is the CSS
  // frame's plane (z = 0), so the outline lands exactly where the frame's does.
  const depth = short * thickness, round = short * .016;
  const body = new THREE.ExtrudeGeometry(roundedRect(W - 2 * round, H - 2 * round, Math.max(1, radius - round)), {
    depth: Math.max(1, depth - 2 * round), bevelEnabled: true, bevelThickness: round, bevelSize: round, bevelSegments: 16, curveSegments: 48,
  });
  body.translate(0, 0, -depth + round);
  // Like a Galaxy S25 in white: a flat frame (the extrusion's sides) around a white glass face.
  const face = new THREE.MeshPhysicalMaterial({color: 0xffffff, roughness: .25, metalness: 0, clearcoat: 1, clearcoatRoughness: .15});
  const frame = new THREE.MeshPhysicalMaterial({color: 0xffffff, roughness: .5, metalness: 0, clearcoat: .3, clearcoatRoughness: .4});
  phone.add(new THREE.Mesh(body, [face, frame]));

  // The screen: the capture on a rounded rectangle just in front of the face, unlit so it keeps its colors.
  const img = front.querySelector('.screen img');
  const texture = await new THREE.TextureLoader().loadAsync(img.currentSrc || img.src);
  texture.colorSpace = THREE.SRGBColorSpace;
  texture.anisotropy = renderer.capabilities.getMaxAnisotropy();
  const sw = W - 2 * bezel, sh = H - 2 * bezel;
  const screen = new THREE.ShapeGeometry(roundedRect(sw, sh, Math.max(1, radius - bezel)), 48);
  const uv = screen.attributes.uv, pos = screen.attributes.position;
  for (let i = 0; i < uv.count; i++) uv.setXY(i, pos.getX(i) / sw + .5, pos.getY(i) / sh + .5);
  screen.translate(0, 0, .6);
  phone.add(new THREE.Mesh(screen, new THREE.MeshBasicMaterial({map: texture, toneMapped: false})));

  // The glass: a soft reflection falling across the face from the upper left, as on a product shot.
  const sheen = document.createElement('canvas');
  sheen.width = 64; sheen.height = 256;
  const g = sheen.getContext('2d'), grad = g.createLinearGradient(0, 0, 64, 256);
  grad.addColorStop(0, 'rgba(255,255,255,.1)'); grad.addColorStop(.38, 'rgba(255,255,255,.025)');
  grad.addColorStop(.39, 'rgba(255,255,255,0)'); grad.addColorStop(1, 'rgba(255,255,255,0)');
  g.fillStyle = grad; g.fillRect(0, 0, 64, 256);
  const glass = new THREE.ShapeGeometry(roundedRect(sw, sh, Math.max(1, radius - bezel)), 48);
  const guv = glass.attributes.uv, gpos = glass.attributes.position;
  for (let i = 0; i < guv.count; i++) guv.setXY(i, gpos.getX(i) / sw + .5, gpos.getY(i) / sh + .5);
  glass.translate(0, 0, 1.2);
  const sheenTexture = new THREE.CanvasTexture(sheen);
  sheenTexture.colorSpace = THREE.SRGBColorSpace;
  phone.add(new THREE.Mesh(glass, new THREE.MeshBasicMaterial({map: sheenTexture, transparent: true, depthWrite: false, toneMapped: false})));

  renderer.render(scene, camera);
  front.style.visibility = 'hidden';
  return canvas;
}
