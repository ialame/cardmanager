import rest from "@/rest";
import {ref, Ref} from "vue";
import {matches} from "lodash";
import {AsyncPredicate} from "@/types/Functions";
import log from "loglevel";
import {asyncFilter} from "@/utils";
import {DeepPartial} from "@/types/deep";
import {HistoryTreeDTO} from "@/types/HistoryTreeDTO";

type IdGetter<T, Id> = (item?: T) => Id | undefined;
export class Service<T extends object, Id> {

    private readonly _url: string;
    private readonly _key: string;
    private readonly _idGetter: IdGetter<T, Id>;
    private readonly _list: Ref<T[]> = ref([]);

    private _init: boolean = false;
    private _flush: Promise<void> = Promise.resolve();
    private _fetching: boolean = false;
    private _batching: boolean = false;

    constructor(url: string, idGetter?: IdGetter<T, Id>) {
        this._url = url.endsWith("/") ? url.slice(0, -1) : url;
        this._key = this._url.replaceAll("/", "_").slice(1);
        this._idGetter = idGetter ?? ((item?: T) => item ? (item as any).id : undefined);

        try {
            this._list.value = JSON.parse(localStorage.getItem(this._key) ?? "[]") ?? [];
        } catch (e) {
            this._list.value = [];
        }
    }

    public get all(): Ref<T[]> {
        if (!this._init) {
            this.fetch();
        }
        return this._list;
    }

    public async save(item: T): Promise<T> {
        await this.flush();

        const id = this._idGetter(item);
        const result = await rest.fetch(id ? "PUT" : "POST", this._url, {data: item});

        log.debug("Saved", this._key, item);
        if (!this._batching) {
            this.fetch();
            await this.flush();
        }
        return this.get(result || id);
    }

    public async merge(item: T, ids?: Id[]): Promise<T> {
        await this.flush();

        const id = this._idGetter(item);
        const result = await rest.put(`${this._url}/${(ids ?? []).join(',')}/merge`, {data: item})

        log.debug("Merged", this._key, item, ids);
        if (!this._batching) {
            this.fetch();
            await this.flush();
        }
        return this.get(result || id);
    }

    public async delete(id: Id): Promise<void> {
        await this.flush();
        await rest.delete(`${this._url}/${id}`);
        log.debug("Deleted", this._key, id);
        if (!this._batching) {
            this.fetch();
            await this.flush();
        }
    }

    public async get(id: Id): Promise<T> {
        await this.flush();

        const inList = this.all.value.find(item => this._idGetter(item) === id);

        if (inList) {
            return inList;
        }

        const found = await rest.get(`${this._url}/${id}`);

        if (found) {
            this.fetch();
        }
        return found;
    }

    public async history(id: Id): Promise<HistoryTreeDTO<T>> {
        return await rest.get(`${this._url}/${id}/history`);
    }

    public async find(value: DeepPartial<T> | AsyncPredicate<T>): Promise<T[]> {
        const f = typeof value === "function" ? value : matches(value);

        if (!this._init) {
            this.fetch();
        }
        await this.flush();
        return await asyncFilter(this.all.value, async v => await f(v));
    }

    public async batch(fn: (service: Service<T, Id>) => Promise<void>) {
        await this.flush();
        this._batching = true;
        await fn(this);
        this.fetch();
        await this.flush();
        this._batching = false;
    }

    public async flush() {
        return this._flush;
    }

    public getId(item?: T): Id | undefined {
        return this._idGetter(item);
    }

    protected fetch() {
        if (this._fetching) {
            return;
        }

        this._fetching = true;
        this._flush = this.asyncFetch()
            .catch(e => log.error(e))
            .finally(() => {
                this._init = true;
                this._fetching = false;
            });
    }

    private async asyncFetch() {
        const list = await rest.get(this._url) || [];

        this._list.value = list;
        localStorage.setItem(this._key, JSON.stringify(list));
        log.debug("Fetched", this._key, list);
    }
}
