import {Service} from "@/types";
import {mockServiceCalls} from "@tu/types/ServiceTestHelper";
import {beforeEach, describe, expect, it} from 'vitest';


type TestType = {
    id?: number;
    value: string;
}

let testService: Service<TestType, number>;

beforeEach(() => {
    testService = new Service<TestType, number>('/api/test');

    mockServiceCalls(testService, [
        {id: 1, value: 'test1'},
        {id: 2, value: 'test2'},
        {id: 3, value: 'test3'},
    ]);
});
describe('Service.all', () => {
    it('is 1, 2, 3', async () => {
        await testService.flush();
        expect(testService.all.value).toEqual([
            {id: 1, value: 'test1'},
            {id: 2, value: 'test2'},
            {id: 3, value: 'test3'},
        ]);
    });
});
describe('Service.save', () => {
    it('add 4 to all values', async () => {
        await testService.save({value: 'test4'});
        await testService.flush();
        expect(testService.all.value).toEqual([
            {id: 1, value: 'test1'},
            {id: 2, value: 'test2'},
            {id: 3, value: 'test3'},
            {value: 'test4'}
        ]);
    });
    it('update 1', async () => {
        const o = await testService.save({id: 1, value: 'test1bis'});

        await testService.flush();
        expect(o.id).toBe(1);
        expect(testService.all.value).toEqual([
            {id: 1, value: 'test1bis'},
            {id: 2, value: 'test2'},
            {id: 3, value: 'test3'}
        ]);
    });
});
describe('Service.delete', () => {
    it('removes 1 from all values', async () => {
        await testService.delete(1);
        await testService.flush();
        expect(testService.all.value).toEqual([
            {id: 2, value: 'test2'},
            {id: 3, value: 'test3'},
        ]);
    });
});
describe('Service.get', () => {
    it('returns 2', async () => {
        const b = await testService.get(2);

        expect(b).toEqual({id: 2, value: 'test2'});
    });
});
