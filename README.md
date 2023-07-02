# Project Archival

I feel as though [Axolotl Client](https://github.com/AxolotlClient) ([Website](https://github.com/AxolotlClient/), [Discord](https://discord.gg/WyMjeX3vka)) is something I'd prefer to work on now. This project will continue working as intended (with the exception of bugs of course 🫠)!

Overall this project had a few problems and limitations:
- Not making use of a modloader, therefore integrations for mods had to be made manually. In the case of OptiFine, due to it being closed source and "all rights reserved" it had to be automatically downloaded in the background in a pretty hacky way. ReplayMod was just a mess to integrate, I didn't really ask for permission (I don't think I felt like asking at the time :P). EntityCulling was pretty easy to integrate however the author understandably relicensed it after an incident involving Lunar Client.
- Only being available for 1.8. I tried to make it work for multiple versions however I... gave up. I worked on another client mod for a while for a newer version and thought about trying to integrate something similar to multiconnect/viafabric, but of course that was something which could be problematic on servers...
- Messy code and development which somehow worked. It went through different structures including: a horrible Gradle mess which I couldn't get to work, MCP, a horrible Gradle mess which somehow worked now that I'd found some other projects to use as reference making use of LaunchWrapper, and finally Fabric's Loom plugin with Quilt's patches, Legacy Fabric's mappings and a custom mixin service and class loader. The UI code was very concerning indeed...
- To launch or not to launch: very messy launcher development, first swing launcher, then Electron launcher which was just worse but at least I could get MS auth working, then Qt launcher, and finally no launcher.
- Despite quite a lot of contributions I was still the only collaborator on this repository! However contributions introducing features were only pretty recent.

However, I did make something which I feel worked pretty nicely! Thanks for all the support and contributions! There are still open Pull Requests so I'm sorry to abandon this project. Fortunately I'm in contact with the authors.

I worked on this in some form from 2020 to 2023 which was kind of a while! goodbye :'(

[Original Discord Server](https://discord.gg/TSAkhgXNbK); I'll probably make it read-only at some point, if so feel free to DM me if you wish to contact (my username is the same as on GitHub). Otherwise I may keep the off-topic channels open for a bit?
