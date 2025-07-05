<template>
    <MergeButton v-if="isActive" side="left" @click="setValue()" @hover="hover" :disabled="!!source && compare(value, source)" />
</template>

<script lang="ts" setup>
import MergeButton from "@components/form/MergeButton.vue";
import {computed} from "vue";
import {cloneDeep, get, isEmpty, isEqual, isNil, set, toPath} from "lodash";
import {getRules} from "@/validation";
import {getParentPath, Path, pathToString} from "@/path";
import log from "loglevel";
import {useAdvancedFormSide} from "@components/form/advanced/merge/logic";
import {useAdvancedFormContext} from "@components/form/advanced/logic";

interface Props {
    path: Path;
}

const props = defineProps<Props>();

const currentSide = useAdvancedFormSide();
const context = useAdvancedFormContext();

const hover = (v: boolean) => {
    if (v) {
        context.hoverPath.value = props.path;
    } else if (context.hoverPath.value === props.path) {
        context.hoverPath.value = "";
    }
}
const isActive = computed(() => currentSide.value !== -1 && !context.disabled.value);
const mergeSource = computed(() => context.mergeSources.value[currentSide.value]);
const source = computed(() => get(mergeSource.value, props.path));
const value = computed(() => get(context.data.value, props.path));
const setValue = () => {
    const copy = cloneDeep(context.data.value) as any;
    let p: Path = toPath(props.path);
    let parent: Path = getParentPath(p);

    while (isNil(get(copy, parent)) && !isEmpty(parent)) {
        p = parent;
        parent = getParentPath(p);
    }
    log.info(`Setting value (merge) '${pathToString(p)}'`, source.value);
    set(copy, p, cloneDeep(get(mergeSource.value, p)));
    context.update(copy);
}
const compare = (v1: any, v2: any) => {
    const c = getRules(context.rules.value, props.path)?.comparator || isEqual;

    return c(v1, v2);
}
</script>
