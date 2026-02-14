# Construction Wand

Construction Wand is a Minecraft plugin based on Paper that provides players with powerful wands,
allowing them to place or break multiple blocks at once, greatly improving building and excavation efficiency.

## Features

### Building Wand
- Place multiple blocks at once
- Two modes available:
    - Normal mode: Area can be formed by any blocks
    - Block strict mode: Area can only be formed by one type of block
- Supports permission checks and game mode restrictions

### Breaking Wand
- Break multiple blocks at once
- Area can only be formed by one type of block
- Supports permission checks and game mode restrictions

### Projection Display
- Displays a preview of blocks to be placed or broken when players aim at blocks with wands
- Preview uses semi-transparent glass effect for clear visibility

### Axis Control
- Switch wand axis restriction mode by swapping off-hand items
- Supports X, Y, Z axis restrictions and unrestricted mode

## Usage

1. **Crafting Wands**: Craft various wands according to the recipes
2. **Using Building Wands**:
    - Hold the building wand and right-click on target blocks
    - The wand will automatically calculate the placement area based on the aimed face and adjacent same blocks
    - In survival mode, corresponding blocks will be consumed
3. **Using Breaking Wands**:
    - Hold the breaking wand and right-click on target blocks
    - The wand will automatically calculate the breaking area based on the aimed face and adjacent same blocks
    - In survival mode, corresponding blocks will be obtained
4. **Switching Axis Mode**:
    - When holding a wand, use the F key (swap off-hand items) to switch axis restriction mode
    - Supports X, Y, Z axis restrictions and unrestricted mode

## Multi-language Support

The plugin supports multiple languages, including:
- English (en)

## Requirements

- Minecraft Paper server (1.21+)
- Java 21 or higher
- [Rebar](https://github.com/pylonmc/rebar) prerequisite plugin

## Notes

1. Large-range wands may impact server performance. Please adjust range limits according to server configuration
2. Ensure you have sufficient permissions when using wands, otherwise operations will be cancelled
3. Ensure you have enough blocks in your inventory when using building wands
4. Server administrators can adjust the number of blocks players can modify when using wands through the `limit-blocks` setting in the configuration file
