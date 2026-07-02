#!/usr/bin/env python3
from __future__ import annotations

import math
import random
import sys
from pathlib import Path
from typing import Callable

from PIL import Image, ImageDraw, ImageEnhance, ImageFilter

ROOT = Path(__file__).resolve().parents[1]
BG = ROOT / "assets" / "backgrounds"
CITIES_DIR = ROOT / "assets" / "cities"
STYLE_TILE = BG / "perm_tile.png"
SCR_W, SCR_H = 1280, 720
FINISH_W = 5120
BLEND_W = 420
SEAM_W = 100

DrawFn = Callable[[ImageDraw.ImageDraw, int, int, random.Random], None]


def blend_pixels(a: tuple[int, int, int], b: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    return (
        int(a[0] * (1.0 - t) + b[0] * t),
        int(a[1] * (1.0 - t) + b[1] * t),
        int(a[2] * (1.0 - t) + b[2] * t),
    )


def cover_crop(img: Image.Image, width: int, height: int, focus_x: float = 0.5) -> Image.Image:
    sw, sh = img.size
    scale = max(width / sw, height / sh)
    new_w = max(width, int(sw * scale))
    new_h = max(height, int(sh * scale))
    resized = img.resize((new_w, new_h), Image.Resampling.LANCZOS)
    left = int((new_w - width) * focus_x)
    top = (new_h - height) // 2
    left = max(0, min(left, new_w - width))
    return resized.crop((left, top, left + width, top + height))


def stylize_city(img: Image.Image, saturation: float = 1.18) -> Image.Image:
    img = ImageEnhance.Color(img).enhance(saturation)
    img = ImageEnhance.Brightness(img).enhance(1.06)
    img = ImageEnhance.Contrast(img).enhance(1.05)
    return img


def apply_sky_gradient(img: Image.Image, top: tuple[int, int, int], bottom: tuple[int, int, int], path_top_y: int) -> None:
    px = img.load()
    for y in range(path_top_y):
        t = y / max(path_top_y - 1, 1)
        for x in range(SCR_W):
            base = px[x, y]
            sky = blend_pixels(top, bottom, t)
            px[x, y] = blend_pixels(base, sky, 0.35)


def tint_grass(img: Image.Image, path_top_y: int, color: tuple[int, int, int], strength: float = 0.22) -> None:
    px = img.load()
    for y in range(path_top_y, SCR_H):
        for x in range(SCR_W):
            px[x, y] = blend_pixels(px[x, y], color, strength)


def draw_birch(draw: ImageDraw.ImageDraw, x: int, base_y: int, h: int, rng: random.Random) -> None:
    draw.rectangle((x + 4, base_y - h, x + 10, base_y), fill=(210, 205, 190))
    for i in range(3, h, 14):
        draw.line((x + 2, base_y - i, x + 12, base_y - i), fill=(80, 80, 80), width=1)
    draw.ellipse((x - 18, base_y - h - 20, x + 32, base_y - h + 30), fill=(70, 150, 70))


def draw_pine(draw: ImageDraw.ImageDraw, x: int, base_y: int, h: int, dark: tuple[int, int, int] = (35, 95, 55)) -> None:
    draw.rectangle((x + 8, base_y - h // 3, x + 12, base_y), fill=(90, 60, 35))
    for i, w in enumerate((50, 42, 34, 26)):
        top = base_y - h + i * (h // 5)
        draw.polygon([(x + 10, top), (x + 10 - w // 2, top + h // 5 + 8), (x + 10 + w // 2, top + h // 5 + 8)], fill=dark)


def draw_lamp(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.rectangle((x + 6, base_y - 90, x + 10, base_y), fill=(60, 60, 65))
    draw.rectangle((x, base_y - 105, x + 16, base_y - 92), fill=(240, 230, 150))


def draw_bench(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.rectangle((x, base_y - 22, x + 46, base_y - 16), fill=(120, 75, 40))
    draw.rectangle((x + 4, base_y - 16, x + 8, base_y), fill=(90, 55, 30))
    draw.rectangle((x + 38, base_y - 16, x + 42, base_y), fill=(90, 55, 30))


def draw_duck(draw: ImageDraw.ImageDraw, x: int, y: int) -> None:
    draw.ellipse((x, y, x + 22, y + 14), fill=(255, 210, 40))
    draw.ellipse((x + 14, y - 6, x + 26, y + 6), fill=(255, 210, 40))
    draw.polygon([(x + 24, y + 2), (x + 32, y + 4), (x + 24, y + 8)], fill=(230, 140, 40))


def draw_squirrel(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.ellipse((x, base_y - 18, x + 20, base_y - 4), fill=(160, 90, 45))
    draw.polygon([(x + 2, base_y - 16), (x - 8, base_y - 28), (x + 6, base_y - 20)], fill=(150, 80, 40))


def draw_horse(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.rectangle((x + 10, base_y - 34, x + 28, base_y - 18), fill=(80, 55, 35))
    draw.rectangle((x + 24, base_y - 44, x + 34, base_y - 30), fill=(80, 55, 35))
    draw.line([(x + 12, base_y - 18), (x + 12, base_y)], fill=(50, 35, 25), width=3)
    draw.line([(x + 24, base_y - 18), (x + 24, base_y)], fill=(50, 35, 25), width=3)


def draw_eagle(draw: ImageDraw.ImageDraw, x: int, y: int) -> None:
    draw.polygon([(x, y + 8), (x - 20, y + 4), (x - 8, y + 10)], fill=(45, 40, 38))
    draw.polygon([(x + 10, y + 8), (x + 30, y + 4), (x + 18, y + 10)], fill=(45, 40, 38))
    draw.ellipse((x, y, x + 12, y + 12), fill=(35, 32, 30))


def draw_cat(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.ellipse((x, base_y - 16, x + 24, base_y - 2), fill=(180, 120, 60))
    draw.polygon([(x + 4, base_y - 16), (x + 2, base_y - 26), (x + 10, base_y - 16)], fill=(180, 120, 60))
    draw.polygon([(x + 14, base_y - 16), (x + 12, base_y - 26), (x + 20, base_y - 16)], fill=(180, 120, 60))


def draw_swans(draw: ImageDraw.ImageDraw, x: int, y: int) -> None:
    draw.ellipse((x, y + 6, x + 28, y + 18), fill=(245, 245, 250))
    draw.ellipse((x + 18, y - 4, x + 34, y + 10), fill=(245, 245, 250))
    draw.line([(x + 30, y + 2), (x + 38, y - 6)], fill=(240, 240, 245), width=2)


def draw_mountain_range(draw: ImageDraw.ImageDraw, base_y: int, color: tuple[int, int, int]) -> None:
    pts = [(0, base_y), (180, base_y - 90), (320, base_y - 40), (520, base_y - 120),
           (720, base_y - 55), (920, base_y - 110), (1100, base_y - 35), (SCR_W, base_y - 80), (SCR_W, base_y)]
    draw.polygon(pts, fill=color)


def draw_kremlin_towers(draw: ImageDraw.ImageDraw, base_y: int, color: tuple[int, int, int]) -> None:
    for x, h, w in ((420, 95, 36), (560, 110, 42), (700, 88, 34), (860, 100, 38)):
        draw.rectangle((x, base_y - h, x + w, base_y), fill=color)
        draw.polygon([(x + w // 2 - 8, base_y - h - 18), (x + w // 2 + 8, base_y - h - 18), (x + w // 2, base_y - h - 34)], fill=color)


def draw_minarets(draw: ImageDraw.ImageDraw, base_y: int, color: tuple[int, int, int]) -> None:
    for x in (380, 520, 680, 820):
        draw.rectangle((x, base_y - 120, x + 14, base_y), fill=color)
        draw.ellipse((x - 6, base_y - 132, x + 20, base_y - 108), fill=color)


def draw_tv_tower(draw: ImageDraw.ImageDraw, x: int, base_y: int, color: tuple[int, int, int]) -> None:
    draw.rectangle((x + 8, base_y - 140, x + 14, base_y), fill=color)
    draw.polygon([(x - 20, base_y - 150), (x + 42, base_y - 150), (x + 11, base_y - 175)], fill=color)


def draw_canal_bridge(draw: ImageDraw.ImageDraw, base_y: int) -> None:
    draw.rectangle((0, base_y - 8, SCR_W, base_y), fill=(70, 130, 180))
    draw.rectangle((340, base_y - 48, 360, base_y - 8), fill=(180, 180, 190))
    draw.rectangle((920, base_y - 48, 940, base_y - 8), fill=(180, 180, 190))
    draw.arc((300, base_y - 70, 400, base_y - 10), 180, 0, fill=(160, 160, 170), width=4)
    draw.arc((880, base_y - 70, 980, base_y - 10), 180, 0, fill=(160, 160, 170), width=4)


def draw_church_dome(draw: ImageDraw.ImageDraw, x: int, base_y: int, color: tuple[int, int, int]) -> None:
    draw.rectangle((x, base_y - 60, x + 50, base_y), fill=(220, 215, 200))
    draw.ellipse((x + 5, base_y - 95, x + 45, base_y - 55), fill=color)
    draw.polygon([(x + 22, base_y - 115), (x + 28, base_y - 115), (x + 25, base_y - 128)], fill=(220, 180, 40))


def draw_perm_river(draw: ImageDraw.ImageDraw, base_y: int) -> None:
    draw.polygon([(0, base_y), (SCR_W, base_y - 12), (SCR_W, base_y + 28), (0, base_y + 18)], fill=(55, 120, 165))


def draw_perm_gazebo(draw: ImageDraw.ImageDraw, x: int, base_y: int) -> None:
    draw.polygon([(x, base_y - 55), (x + 50, base_y - 55), (x + 25, base_y - 78)], fill=(130, 85, 50))
    draw.rectangle((x + 8, base_y - 55, x + 12, base_y), fill=(100, 65, 38))
    draw.rectangle((x + 38, base_y - 55, x + 42, base_y), fill=(100, 65, 38))


def decor_perm(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_perm_river(d, 250)
    draw_pine(d, 120, 280, 110)
    draw_pine(d, 980, 285, 95, (30, 85, 50))
    draw_perm_gazebo(d, 640, 285)
    draw_bench(d, 380, 285)
    draw_squirrel(d, 820, 285)


def decor_tomsk(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    for x in (80, 220, 1040, 1160):
        draw_birch(d, x, 275, rng.randint(95, 120), rng)
    draw_church_dome(d, 520, 270, (180, 60, 50))
    draw_lamp(d, 760, 278)
    draw_cat(d, 910, 278)


def decor_barnaul(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_mountain_range(d, 250, (100, 120, 140))
    draw_pine(d, 180, 285, 85, (25, 75, 45))
    draw_eagle(d, 720, 120)
    draw_bench(d, 450, 285)


def decor_kurgan(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_mountain_range(d, 260, (130, 145, 120))
    draw_horse(d, 560, 285)
    for x in range(100, SCR_W, 180):
        d.line([(x, 288), (x + 40, 285)], fill=(160, 170, 90), width=2)


def decor_ekb(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_tv_tower(d, 600, 275, (90, 95, 105))
    for x in (200, 340, 860, 1000):
        d.rectangle((x, 200, x + 55, 275), fill=(110, 115, 125))
        d.rectangle((x + 10, 170, x + 45, 200), fill=(120, 125, 135))
    draw_lamp(d, 480, 280)


def decor_kazan(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_minarets(d, 255, (60, 130, 90))
    draw_church_dome(d, 720, 268, (50, 160, 120))
    draw_bench(d, 1020, 282)


def decor_moscow(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_kremlin_towers(d, 255, (180, 50, 45))
    draw_church_dome(d, 180, 268, (255, 210, 50))
    draw_lamp(d, 1040, 280)
    draw_cat(d, 420, 282)


def decor_novgorod(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    d.rectangle((300, 210, 520, 272), fill=(200, 195, 175))
    draw_church_dome(d, 360, 268, (40, 80, 160))
    draw_church_dome(d, 880, 270, (180, 50, 45))
    draw_bench(d, 640, 283)


def decor_spb(d: ImageDraw.ImageDraw, w: int, h: int, rng: random.Random) -> None:
    draw_canal_bridge(d, 248)
    draw_church_dome(d, 520, 265, (180, 170, 160))
    draw_swans(d, 180, 230)
    draw_lamp(d, 1100, 282)


CITY_CONFIG: list[tuple[str, int, float, int, tuple, tuple, tuple, DrawFn]] = [
    ("perm", 1, 0.38, 295, (120, 195, 230), (190, 225, 245), (55, 130, 70), decor_perm),
    ("tomsk", 2, 0.35, 292, (135, 200, 240), (210, 235, 250), (65, 145, 75), decor_tomsk),
    ("barnaul", 3, 0.40, 288, (150, 205, 245), (220, 235, 245), (80, 150, 70), decor_barnaul),
    ("kurgan", 4, 0.38, 300, (170, 210, 250), (230, 240, 245), (120, 160, 75), decor_kurgan),
    ("ekb", 5, 0.42, 293, (125, 180, 225), (200, 225, 240), (50, 120, 65), decor_ekb),
    ("kazan", 6, 0.30, 290, (110, 190, 235), (205, 230, 248), (45, 135, 60), decor_kazan),
    ("moscow", 7, 0.45, 294, (100, 165, 220), (195, 220, 240), (40, 125, 55), decor_moscow),
    ("novgorod", 8, 0.35, 291, (130, 185, 230), (215, 232, 245), (60, 140, 70), decor_novgorod),
    ("spb", 9, 0.40, 296, (95, 155, 210), (180, 210, 235), (35, 115, 50), decor_spb),
]


def load_style_base() -> Image.Image:
    if not STYLE_TILE.exists():
        raise FileNotFoundError(STYLE_TILE)
    return cover_crop(Image.open(STYLE_TILE).convert("RGB"), SCR_W, SCR_H)


def load_city_layer(city_index: int, focus_x: float = 0.5) -> Image.Image:
    path = CITIES_DIR / f"{city_index}.png"
    if not path.exists():
        raise FileNotFoundError(path)
    return stylize_city(cover_crop(Image.open(path).convert("RGB"), SCR_W, SCR_H, focus_x))


def compose_park_tile(style: Image.Image, city: Image.Image, path_top_y: int,
                      sky_top: tuple, sky_bottom: tuple, grass: tuple,
                      decor: DrawFn, seed: int) -> Image.Image:
    out = style.copy()
    opx = out.load()
    cpx = city.load()
    blend_h = 90
    for y in range(SCR_H):
        for x in range(SCR_W):
            if y >= path_top_y:
                continue
            t = 1.0
            if y >= path_top_y - blend_h:
                t = (path_top_y - y) / blend_h
            opx[x, y] = blend_pixels(cpx[x, y], opx[x, y], min(1.0, t * 0.78 + 0.12))

    apply_sky_gradient(out, sky_top, sky_bottom, path_top_y)
    tint_grass(out, path_top_y, grass, 0.18)

    overlay = Image.new("RGBA", (SCR_W, SCR_H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    decor(draw, SCR_W, SCR_H, random.Random(seed))
    out = Image.alpha_composite(out.convert("RGBA"), overlay).convert("RGB")
    return out


def compose_finish_frame(tile_like: Image.Image, city_finish: Image.Image, path_top_y: int) -> Image.Image:
    out = tile_like.copy()
    opx = out.load()
    fpx = city_finish.load()
    start_x = SCR_W // 2 - 80
    for x in range(start_x, SCR_W):
        t = (x - start_x) / max(SCR_W - start_x, 1)
        t = t * t * (3 - 2 * t)
        for y in range(SCR_H):
            if y >= path_top_y:
                base = opx[x, y]
                opx[x, y] = blend_pixels(base, fpx[x, y], t * 0.58)
            else:
                opx[x, y] = blend_pixels(opx[x, y], fpx[x, y], min(1.0, 0.32 + t * 0.58))
    return out


def seam_horizontal(img: Image.Image) -> Image.Image:
    px = img.load()
    for x in range(SEAM_W):
        t = x / max(SEAM_W - 1, 1)
        for y in range(SCR_H):
            px[x, y] = blend_pixels(px[SCR_W - SEAM_W + x, y], px[x, y], t)
    return img


def seam_panel(tile: Image.Image, finish: Image.Image) -> Image.Image:
    panel = finish.copy()
    px = panel.load()
    pt = tile.load()
    for x in range(BLEND_W):
        t = x / max(BLEND_W - 1, 1)
        tx = SCR_W - BLEND_W + x
        for y in range(SCR_H):
            px[x, y] = blend_pixels(pt[tx, y], finish.getpixel((x, y)), t)
    return panel


def finish_to_tile_panel(tile: Image.Image, finish: Image.Image) -> Image.Image:
    panel = tile.copy()
    px = panel.load()
    pf = finish.load()
    for x in range(BLEND_W):
        t = x / max(BLEND_W - 1, 1)
        fx = SCR_W - BLEND_W + x
        for y in range(SCR_H):
            px[x, y] = blend_pixels(pf[fx, y], tile.getpixel((x, y)), t)
    return panel


def build_city(city_id: str, city_index: int, finish_focus: float, path_top_y: int,
               sky_top: tuple, sky_bottom: tuple, grass: tuple, decor: DrawFn) -> None:
    style = load_style_base()
    city_mid = load_city_layer(city_index, 0.5)
    city_finish = load_city_layer(city_index, finish_focus)

    seed = sum(ord(c) for c in city_id) * 17
    tile = seam_horizontal(compose_park_tile(
        style, city_mid, path_top_y, sky_top, sky_bottom, grass, decor, seed))
    finish_frame = compose_finish_frame(tile, city_finish, path_top_y)

    tile.save(BG / f"{city_id}_tile.png", optimize=True)
    finish_frame.save(BG / f"{city_id}.png", optimize=True)

    panels = [seam_panel(tile, finish_frame), finish_frame, finish_to_tile_panel(tile, finish_frame), tile]
    wide = Image.new("RGB", (FINISH_W, SCR_H))
    x = 0
    idx = 0
    while x < FINISH_W:
        wide.paste(panels[min(idx, len(panels) - 1)], (x, 0))
        x += SCR_W
        idx += 1
    wide.save(BG / f"{city_id}_finish.png", optimize=True)
    print(f"  {city_id}")


def main() -> None:
    targets = CITY_CONFIG
    if len(sys.argv) > 1:
        wanted = set(sys.argv[1:])
        targets = [t for t in CITY_CONFIG if t[0] in wanted]

    print("Building diverse city park backgrounds...")
    for row in targets:
        build_city(*row)
    print("Done.")


if __name__ == "__main__":
    main()
