# CURRENT RELEASE: v1.1.1
## About
#### Fabric 1.21.5 Mod for Hypixel Skyblock

## Usage
`/skyblockcheats` or alias `/sbc`

## Features
#### Grotto Search
`/sbc grottosearch [clear / list / scan / toggle]` <br>
Searches for Magenta Stained Glass and Panes (only in Fairy Grottos)
- Highlights the found blocks
- List out all highlighted blocks
- Togglable Search Loop with **server hop**
- Single click block search
- Ping when found
- Copiable coordinates

#### Uptime
`/sbc uptime` <br>
Displays Server Uptime
- Displayed in Day count, Hours, Minutes, Ticks

#### TPS
`/sbc tps` <br>
Displays Server TPS

#### Ghost Block
Makes ghost blocks without tools
- Turn on Ghost Block in config and hold keybind to create ghost blocks when you mine!

#### Automelody
Automatically completes melody harp song
- Very slightly ping dependent

#### Send Coords
`/sbc sendcoords` <br>
Sends your coords like in Patcher
- Make sure to change your chat channel before using

#### Look At
`/sbc look [block / pos] [x] [y], [z]` <br>
Quick util to face a certain position
- Look at blocks and specific positions

#### Play Sound
`/sbc playsound [sound] [volume] [pitch]` <br>
Plays a sound effect
- Useful to test out sounds such as for ping-on-found

#### Server Tracker
`/sbc serverhistory [list / clear]` <br>
Tracks servers and notifies when a server was visited recently
- Useful to avoid duplicate servers or to find a server you were on before

## Config
`/sbc config list`: Lists all config options and values
`/sbc config set [name] [new value]`: Changes configs
`/sbc config reset [name / all]`: Resets config options to default

<b>delay</b>: Time in seconds between each server hop in the Grotto Search loop (0 = no delay) <br>
Default: 0 <br>
<b>rgba-block-color</b>: Color of the highlighted blocks in RGBA format (r-g-b-a) <br>
Default: 255-103-103-0.6 <br>
<b>solid-highlight</b>: Whether to use solid color for highlighted blocks (true/false) <br>
Default: true <br>
<b>outline-weight</b>: Thickness of the outline for highlighted blocks (I have no idea the unit) <br>
Default: 10.0 <br>
<b>ping-on-found</b>: Whether to play a sound when a block is found in Grotto Search (true/false) <br>
Default: true <br>
<b>ping-sound</b>: Sound to play when a block is found in Grotto Search (full or incomplete id - minecraft:block.anvil.land or block.anvil.land are both acceptable) <br>
Default: minecraft:block.anvil.land <br>
<b>ping-volume</b>: Volume of the ping sound (0.0 to 1.0) <br>
Default: 1.0 <br>
<b>ping-pitch</b>: Pitch of the ping sound (0.0 to 2.0) <br>
Default: 1.0 <br>
<b>warp-in</b>: Location to warp in when using the Grotto Search <br>
Default: ch <br>
<b> warp-out</b>: Location to warp out when using the Grotto Search <br>
Default: forge <br>
<b>filter-Y</b>: Whether to filter out blocks based on Y coordinate in Grotto Search (true/false) <br>
Default: false <br>
<b>filter-Y-max</b>: Maximum Y coordinate to include in Grotto Search if filter-Y is true <br>
Default: 64 <br>
<b>auto-melody</b>: Whether to automatically complete the melody harp song (true/false <br>
Default: false <br>
<b>ghost-block</b>: Whether to enable ghost blocks without tools (true/false) <br>
Default: false <br>
<b>max-log-time</b>: Maximum time in seconds to keep server logs for Server History <br>
Default: 600 <br>

## Contact
DM `windows1267` on discord for any questions, issues, or suggestions