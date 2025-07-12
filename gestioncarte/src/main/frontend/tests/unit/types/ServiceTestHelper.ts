import {Service} from "@/types";
import createMocker, {MockResponseInitFunction} from "vitest-fetch-mock";
import {cloneDeep} from "lodash";
import {vi} from "vitest";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

const serviceMap: Record<string, MockResponseInitFunction> = {};
let started = false;

function findKey(url: string) {
    return Object.keys(serviceMap).find(u => url.startsWith(u)) || '';
}

function start() {
    if (started) {
        return;
    }

    fetchMock.enableMocks();
    fetchMock.mockIf(r => !!findKey(r.url), r => serviceMap[findKey(r.url)](r));
}

export function mockServiceCalls<T extends object, Id>(service: Service<T, Id>, v: T[]) {
    const values = cloneDeep(v);
    const url = `http://localhost${service['_url']}`;
    const idGetter = service['_idGetter'];

    function is(v: T, id: string) {
        const idValue = idGetter(v);

        return idValue && idValue.toString() === id;
    }

    serviceMap[url] = async r => {
        const id = r.url.substring(url.length + 1);

        if (r.method === "GET") {
            if (id) {
                const ret = values.find(v => is(v, id));

                if (!ret) {
                    return Promise.reject("Not found");
                }
                return JSON.stringify(ret);
            }
            return JSON.stringify(values);
        } else if (r.method === "POST") {
            const item = await r.json();

            values.push(item);
            return idGetter(item)?.toString() || "";
        } else if (r.method === "PUT") {
            const item = await r.json();

            const id = idGetter(item)?.toString() || "";
            const index = values.findIndex(v => is(v, id));

            values[index] = item;
            return id;
        } else if (r.method === "DELETE") {
            const index = values.findIndex(v => is(v, id));

            values.splice(index, 1);
            return '';
        }
        return Promise.reject("Unknown method");
    };

    start();

    if (service['_fetching']) {
        service['_flush'] = service.flush().then(() => {
            service.all.value = values;
            service['_init'] = true;
        });
    } else {
        service.all.value = values;
        service['_init'] = true;
    }
}
