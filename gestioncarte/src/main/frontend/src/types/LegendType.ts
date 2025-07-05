interface LegendBase {
	text: string;
}

export interface LegendColor extends LegendBase {
	color: string;
}

export interface LegendIconName extends LegendBase {
	iconName: string;
    color?: string;
}

export interface LegendIconSrc extends LegendBase {
	iconSrc: string;
    color?: string;
}

export type LegendType = LegendColor | LegendIconName | LegendIconSrc;

export const getLegendName = (o: LegendType) => (o as LegendIconName).iconName;
export const getLegendSrc = (o: LegendType) => (o as LegendIconSrc).iconSrc;
export const getLegendColor = (o: LegendType) => o?.color ? `text-${o.color}` : '';
