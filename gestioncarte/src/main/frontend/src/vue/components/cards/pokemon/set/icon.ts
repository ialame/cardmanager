import {isString} from "lodash";
import {encodeImage} from "@/image";
import {ExtractedImageDTO} from "@/types";

type SetOrShortName = string | { shortName?: string };
export const getSetIconLink = (set?: SetOrShortName): string => {
    const shortName = isString(set) ? set : set?.shortName;

    return shortName ? `/images/symbole/pokemon/noir/${shortName}.png` : '';
}

export async function getSetIcon(set?: SetOrShortName): Promise<ExtractedImageDTO | undefined> {
    const iconLink = getSetIconLink(set);
    const icon: ArrayBuffer | undefined = iconLink ? await fetch(iconLink)
        .then(response => response.ok ? response.blob() : undefined)
        .then(blob => blob?.arrayBuffer())
        .catch(() => undefined) : undefined;

    if (icon) {
        return {base64Image: encodeImage(icon), source: 'saved'};
    }
    return undefined;
}
