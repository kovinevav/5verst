#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
TILE_PATH = ROOT / "assets" / "backgrounds" / "nsk_tile.png"
SCR_W, SCR_H = 1280, 720
BLEND_W = 100


def blend(a: tuple[int, int, int], b: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    return (
        int(a[0] * (1.0 - t) + b[0] * t),
        int(a[1] * (1.0 - t) + b[1] * t),
        int(a[2] * (1.0 - t) + b[2] * t),
    )


def main() -> None:
    img = Image.open(TILE_PATH).convert("RGB")
    px = img.load()
    for x in range(BLEND_W):
        t = x / max(BLEND_W - 1, 1)
        left_x = x
        right_x = SCR_W - BLEND_W + x
        for y in range(SCR_H):
            px[left_x, y] = blend(px[right_x, y], px[left_x, y], t)
    img.save(TILE_PATH, optimize=True)
    print(f"Seamed tile: {TILE_PATH}")


if __name__ == "__main__":
    main()
