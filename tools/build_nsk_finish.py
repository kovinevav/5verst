#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
BG = ROOT / "assets" / "backgrounds"
SCR_W, SCR_H = 1280, 720
FINISH_W = 5120
BLEND_W = 420


def blend_pixels(a: tuple[int, int, int], b: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    return (
        int(a[0] * (1.0 - t) + b[0] * t),
        int(a[1] * (1.0 - t) + b[1] * t),
        int(a[2] * (1.0 - t) + b[2] * t),
    )


def seam_panel(tile: Image.Image, bridge: Image.Image) -> Image.Image:
    panel = bridge.copy()
    px = panel.load()
    pt = tile.load()
    for x in range(BLEND_W):
        t = x / max(BLEND_W - 1, 1)
        tx = SCR_W - BLEND_W + x
        for y in range(SCR_H):
            px[x, y] = blend_pixels(pt[tx, y], bridge.getpixel((x, y)), t)
    return panel


def bridge_to_tile_panel(tile: Image.Image, bridge: Image.Image) -> Image.Image:
    panel = tile.copy()
    px = panel.load()
    pb = bridge.load()
    for x in range(BLEND_W):
        t = x / max(BLEND_W - 1, 1)
        bx = SCR_W - BLEND_W + x
        for y in range(SCR_H):
            px[x, y] = blend_pixels(pb[bx, y], tile.getpixel((x, y)), t)
    return panel


def build_finish() -> None:
    tile = Image.open(BG / "nsk_tile.png").convert("RGB")
    bridge = Image.open(BG / "nsk.png").convert("RGB")

    finish = Image.new("RGB", (FINISH_W, SCR_H))

    panels = [
        seam_panel(tile, bridge),
        bridge,
        bridge_to_tile_panel(tile, bridge),
        tile,
    ]

    x = 0
    panel_idx = 0
    while x < FINISH_W:
        panel = panels[min(panel_idx, len(panels) - 1)]
        finish.paste(panel, (x, 0))
        x += SCR_W
        panel_idx += 1

    out = BG / "nsk_finish.png"
    finish.save(out, optimize=True)
    print(f"Wrote {out} ({finish.size[0]}×{finish.size[1]})")


if __name__ == "__main__":
    build_finish()
