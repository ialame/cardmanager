import {App, setupDevtoolsPlugin} from '@vue/devtools-api'
import {isArray} from "lodash";
import {InspectorNodeTag} from "@vue/devtools-api/lib/esm/api/api";
import {getCurrentInstance} from "vue";
import {isInBrowser} from "@/vue/composables/ComposablesHelper";
import {DevtoolsPluginApi} from "@vue/devtools-api/lib/esm/api";

const advanceFormLayerId = 'advanced-form';

let devtoolsApi: DevtoolsPluginApi<any> | undefined;
let trackId = 0;

export function addDevtools(app: App) {
    if (typeof window !== 'undefined') {
        setupDevtoolsPlugin({
            id: 'retriever',
            label: 'Professional Card Retriever',
            packageName: 'retriever',
            enableEarlyProxy: true,
            app
        }, api => {
            devtoolsApi = api;
            api.on.visitComponentTree(({treeNode, componentInstance}) => {
                if (isArray(componentInstance.__retriever_devtools)) {
                    componentInstance.__retriever_devtools.forEach((t: InspectorNodeTag) => {
                        treeNode.tags.push(t);
                    });
                }
            });
            api.addTimelineLayer({
                id: advanceFormLayerId,
                label: 'Advanced Form',
                color: 0xc4161c
            });
        });
    }
}

export function addDevtoolTags(...tags: InspectorNodeTag[]) {
    if (isInBrowser()) {
        const instance = getCurrentInstance();

        if (instance) {
            // @ts-expect-error: this is internal
            if (!isArray(instance.__retriever_devtools)) {
                // @ts-expect-error: this is internal
                instance.__retriever_devtools = [];
            }
            // @ts-expect-error: this is internal
            instance.__retriever_devtools.push(...tags);
        }
    }
}
export const advancedFormLayer = {
    addEvent: (event: string, data?: any) => {
        devtoolsApi?.addTimelineEvent({
            layerId: advanceFormLayerId,
            event: {
                time: devtoolsApi.now(),
                title: event,
                data
            }
        });
    },
    trackStart: (label: string, data?: any) => {
        if (!devtoolsApi) {
            return () => {};
        }

        const groupId = 'track' + trackId++

        devtoolsApi.addTimelineEvent({
            layerId: advanceFormLayerId,
            event: {
                time: devtoolsApi.now(),
                data: {
                    label,
                    ...data
                },
                title: label,
                groupId
            }
        })

        return (data2?: any) => {
            if (!devtoolsApi) {
                return;
            }

            devtoolsApi.addTimelineEvent({
                layerId: advanceFormLayerId,
                event: {
                    time: devtoolsApi.now(),
                    data: {
                        label,
                        ...data,
                        ...data2,
                        done: true
                    },
                    title: label,
                    groupId
                }
            })
        }
    }
}
