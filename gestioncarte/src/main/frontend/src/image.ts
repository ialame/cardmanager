import {Buffer} from "buffer";

export const encodeImage = (image?: ArrayBuffer): string => image ? Buffer.from(image).toString('base64') : '';
export const decodeImage = (image?: string): ArrayBuffer | undefined => image ? Buffer.from(image, 'base64').buffer : undefined;
