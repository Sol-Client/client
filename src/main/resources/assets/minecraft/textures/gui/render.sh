flatpak run org.inkscape.Inkscape -w 16 -h 16 $1 -o $(echo "$1" | cut -f 1 -d '.')"_1x.png"
flatpak run org.inkscape.Inkscape -w 32 -h 32 $1 -o $(echo "$1" | cut -f 1 -d '.')"_2x.png"
flatpak run org.inkscape.Inkscape -w 48 -h 48 $1 -o $(echo "$1" | cut -f 1 -d '.')"_3x.png"
flatpak run org.inkscape.Inkscape -w 64 -h 64 $1 -o $(echo "$1" | cut -f 1 -d '.')"_4x.png"
