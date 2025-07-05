import Button from '@/components/Button.vue';
import {identity, pickBy} from 'lodash';
import log from "loglevel";
import {useRaise} from "@/alert";

interface Loadable {
	startLoading: () => void;
	stopLoading: () => void;
}

export type OptionButton = Loadable | InstanceType<typeof Button>;
type Options = {
	/**
	 * @deprecated
	 */
	success?: (a: any) => any;
	/**
	 * @deprecated
	 */
	buttons?: OptionButton | (OptionButton | undefined)[];
	params?: any;
	data?: any;
	raw?: boolean;
};

export default {
	_headers: undefined as (Headers | undefined),
	get: async function(url: string, options?: Options): Promise<any> {
		return this.fetch("GET", url, options);
	},
	post: async function(url: string, options?: Options): Promise<any> {
		return this.fetch("POST", url, options);
	},
	put: async function(url: string, options?: Options): Promise<any> {
		return this.fetch("PUT", url, options);
	},
	delete: async function(url: string, options?: Options): Promise<any> {
		return this.fetch("DELETE", url, options);
	},
	fetch: async function(method: string, url: string, options?: Options): Promise<any> {
		const raise = useRaise();
		const success = options?.success;
		const buttons = options?.buttons;
		const _url = this._createUrl(url);
		const callbacks: Function[] = [];
		let value = undefined;
	
		if (options && options.params) {
			_url.search = new URLSearchParams(pickBy(options.params, (o: any) => o === false || identity(o))).toString();
		}
		if (buttons) {
			const setLoading = (b?: OptionButton) => {
				if (b && b.startLoading && b.startLoading instanceof Function && b.stopLoading && b.stopLoading instanceof Function) {
					b.startLoading();
					callbacks.push(b.stopLoading);
				}
			};
			
			if (buttons instanceof Array) {
				for (const b of buttons) {
					setLoading(b);
				}
			} else {
				setLoading(buttons);
			}
		}

		const request: RequestInit = {
			headers: this._getHeaders(),
			method: method
		};
		if (options?.data) {
			if (typeof(options.data) === 'string' || options.data instanceof String) {
				request.body = options.data as string;
			} else {
				request.body = JSON.stringify(options.data);
			}
		}
		
		const response = await fetch(_url.toString(), request).catch(e => {
			raise.error("Une erreur est survenue lors d'une requête vers le serveur. Celui-ci n'est plus joignable.");
			log.error(e);
		});
		
		if (response) {
			if (response.ok) {
				const text = await response.text();

				if (options?.raw) {
					value = text;
				} else {
					value = text?.length ? JSON.parse(text) : undefined;
				}

				if (success) {
					try {
						await success(value);
					} catch (error) {
						raise.genericError();
						log.error(error);
					}
				}
			} else if (response.status == 401 || response.status == 403) {
				location.reload();
			} else if (response.status == 500) {
				raise.genericError();
			}
		}
		callbacks.forEach(c => c());
		return value;
	},
	_createUrl(url: string): URL {
		try {
			return new URL(url);
		} catch {
			return new URL(url, window.location.toString());
		}
	},
	_getHeaders(): Headers {
		if (!this._headers) {
			const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");
			const csrf = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
			const headers: any = { "Content-Type": "application/json; charset=utf-8" };

			if (csrfHeader && csrf) {
				headers[csrfHeader] = csrf;
			}
			this._headers = new Headers(headers);
		}
		return this._headers;
	}
};
