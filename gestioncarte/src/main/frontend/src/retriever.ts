export const getDateStr = (): string => {
	const date = new Date();

	return "" + date.getFullYear() + (date.getMonth() + 1) + date.getDate() + date.getHours() + date.getMinutes() + date.getSeconds()
}

export const downloadData = (name: string, data: any) => {
	const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data));
	const downloadAnchorNode = document.createElement('a');

	downloadAnchorNode.setAttribute("href", dataStr);
	downloadAnchorNode.setAttribute("download", name);
	document.body.appendChild(downloadAnchorNode);
	downloadAnchorNode.click();
	downloadAnchorNode.remove();
};

export function timeout(ms: number) {
	return new Promise(resolve => setTimeout(resolve, ms));
}
