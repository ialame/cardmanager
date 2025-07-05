import {shallowMount} from "@vue/test-utils";
import {MountingOptions} from "@vue/test-utils/dist/types";
import {expect, it} from 'vitest';

export namespace ComponentTestHelper {
    export const exists = (c: any, options?: MountingOptions<any> & Record<string, any>) => it(`exists`, () => expect(shallowMount(c, options).exists()).toBeTruthy());
}
