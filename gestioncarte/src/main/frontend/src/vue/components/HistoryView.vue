<template>
    <div v-if="modelValue" class="d-flex flex-row">
        <div class="ms-3 history-div" :style="{ 'width': `${width}px`, 'height': `${height}px` }">
            <svg :viewBox="`0 0 ${width} ${height}`" xmlns="http://www.w3.org/2000/svg">
                <template v-for="(dot, i) in commitDots" :key="i">
                    <line v-if="dot.parent" :x1="offset + dot.x * spacingX" :y1="offset + dot.y * spacingY" :x2="offset + dot.parent.x * spacingX" :y2="offset + dot.parent.y * spacingY" class="history-line" />
                </template>
                <template v-for="(dot, i) in commitDots" :key="i">
                    <circle v-if="dot.data === selected" :cx="offset + dot.x * spacingX" :cy="offset + dot.y * spacingY" r="10" class="commit highlight-commit" @click="selected = dot.data" />
                    <circle :cx="offset + dot.x * spacingX" :cy="offset + dot.y * spacingY" r="5" class="commit" :class="dot.class" @click="selected = dot.data" v-element-hover="h => setHover(h, dot)" />
                </template>
            </svg>
        </div>
        <div v-if="selected !== undefined" class="w-100">
            <slot :modelValue="selected.revision.data" :parents="selected.parents.map(p => p.revision.data)" />
        </div>
        <MouseTooltip :show="!!hover">
            {{hover.label}}: {{hover.data.revision.number}}
            <br>
            {{formatDateTime(hover.data.revision.modificationDate)}}
            <template v-if="hover.data.revision.author">
                <br>
                {{hover.data.revision.author}}
            </template>
            <template v-if="hover.data.revision.message">
                <br>
                {{hover.data.revision.message}}
            </template>
        </MouseTooltip>
    </div>
</template>

<script lang="ts" setup>
import {HistoryTreeDTO} from "@/types";
import {computed, ref, watch} from 'vue';
// noinspection ES6UnusedImports
import {vElementHover} from '@vueuse/components';
import MouseTooltip from "@components/tooltip/MouseTooltip.vue";

const spacingX = 30;
const spacingY = 50;
const offset = 10;

interface CommitDot {
    data: HistoryTreeDTO<any>;
    x: number;
    y: number;
    parent?: CommitDot;
    class: string;
    label: string;
}

interface Props {
    modelValue?: HistoryTreeDTO<any>;
}

const props = defineProps<Props>();

const selected = ref<HistoryTreeDTO<any> | undefined>(props.modelValue);
const hover = ref<CommitDot>();
const setHover = (h: boolean, dot: CommitDot) => hover.value = h ? dot : undefined;

const commitDots = computed(() => getCommitDots(props.modelValue));
const getCommitDots = (h?: HistoryTreeDTO<any>, x?: number, y?: number, parent?: CommitDot): CommitDot[] => {
    if (!h) {
        return [];
    }

    const nx = x || 0;
    const ny = y || 0;

    const dot = {
        data: h,
        x: nx,
        y: ny,
        parent,
        class: h.parents.length > 1 ? 'merge-commit' : getClass(h.revision.type),
        label: h.parents.length > 1 ? "Fusion" : getLabel(h.revision.type)
    };
    const value: CommitDot[] = [dot];
    let xOffset = nx;

    h.parents.forEach(p => {
        const dots = getCommitDots(p, xOffset, ny + 1, dot);

        xOffset = Math.max(xOffset, ...dots.map(d => d.x)) + 1;
        value.push(...dots);
    });
    return value;
}
const width = computed(() => Math.max(...commitDots.value.map(d => d.x)) * spacingX + 2 * offset);
const height = computed(() => Math.max(...commitDots.value.map(d => d.y)) * spacingY + 2 * offset);

const getClass = (type?: string) => {
    switch (type) {
        case 'INSERT':
            return 'insert-commit';
        case 'DELETE':
            return 'delete-commit';
        case 'UPDATE':
            return 'update-commit';
        default:
            return '';
    }
};
const getLabel = (type?: string) => {
    switch (type) {
        case 'INSERT':
            return 'Insertion';
        case 'DELETE':
            return 'Suppression';
        case 'UPDATE':
            return 'Modification';
        default:
            return '';
    }
};

const formatDateTime = (date: string) => {
    const d = new Date(date);
    return d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
}

watch(() => props.modelValue, () => {
    if (props.modelValue) {
        selected.value = props.modelValue;
    }
});
</script>

<style lang="scss" scoped>

@import 'src/variables.scss';

.history-line {
    stroke: rgba($secondary, 0.6);
    stroke-width: 2px;
}
.commit {
    cursor: pointer;
}
.highlight-commit {
    fill: rgba($secondary, 0.25);
}
.insert-commit {
    fill: $primary;
}
.update-commit {
    fill: $secondary;
}
.delete-commit {
    fill: $danger;
}
.merge-commit {
    fill: $info;
}
</style>
