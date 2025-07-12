import {isString} from "lodash";

export interface Localization {
	code: LocalizationCode;
	name: string;
	hasOriginalName: boolean;
}
export type LocalizationCode = keyof typeof localizations;
export type LocalizationGroupCode = LocalizationCode | 'west' | 'cn+zh';

export const us: Localization = { code: 'us', name: "Anglais", hasOriginalName: false };
export const jp: Localization = { code: 'jp', name: "Japonais", hasOriginalName: true };
export const fr: Localization = { code: 'fr', name: "Français", hasOriginalName: false };
export const it: Localization = { code: 'it', name: "Italien", hasOriginalName: false };
export const de: Localization = { code: 'de', name: "Allemand", hasOriginalName: false };
export const es: Localization = { code: 'es', name: "Espagnol", hasOriginalName: false };
export const kr: Localization = { code: 'kr', name: "Coréen", hasOriginalName: true };
export const cn: Localization = { code: 'cn', name: "Chinois Traditionnel", hasOriginalName: true };
export const zh: Localization = { code: 'zh', name: "Chinois Simplifié", hasOriginalName: true };
export const ru: Localization = { code: 'ru', name: "Russe", hasOriginalName: true };
export const nl: Localization = { code: 'nl', name: "Hollandais", hasOriginalName: false };
export const pt: Localization = { code: 'pt', name: "Portugais", hasOriginalName: false };

export const localizations = {
	us: us,
	jp: jp,
	fr: fr,
	it: it,
	de: de,
	es: es,
	kr: kr,
	cn: cn,
	zh: zh,
	ru: ru,
	nl: nl,
	pt: pt
};

export function getLocalization(localization: LocalizationCode): Localization {
	return localizations[localization];
}

export function getCode(localization: Localization | LocalizationCode): LocalizationCode {
	if (isString(localization)) {
		return localization;
	}
	return localization.code;
}

const _order = Object.keys(localizations);
export function sortLocalizations(l: LocalizationCode[]): LocalizationCode[] {
	return l.sort((a, b) => _order.indexOf(a) - _order.indexOf(b));
}

export const localizationCodes = _order as LocalizationCode[];
