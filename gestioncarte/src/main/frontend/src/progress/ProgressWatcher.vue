<template>
    <ProgressBar v-if="progress" :value="progress.progress" :max="progress.max" :label="progress.message" />
</template>

<script lang="ts" setup>
import {ProgressBar} from "@/progress";
import {RestComposables} from "@/vue/composables/RestComposables";
import {IProgressTracker} from "@/types";
import {computed, ref, watch} from "vue";
import rest from "@/rest";
import {isNil} from "lodash";
import {useSound} from "@vueuse/sound";
import pingSfx from "../../assets/sound/ping.mp3";

interface Props {
    endpoint: string;
}
interface Emits {
    (e: 'start'): void;
    (e: 'done'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const running = ref(false);
const interval = computed(() => running.value ? 500 : 5000);
const progress = RestComposables.useRestComposable<IProgressTracker | undefined>(() => rest.get(props.endpoint), undefined, { interval: interval });

const { play } = useSound(pingSfx);

const start = () => {
    running.value = true;
    emit('start');
};

watch(progress, value => {
    if (isNil(value)) {
        if (running.value) {
            running.value = false;
            play();
            emit('done');
        }
    } else if (!running.value) {
        start();
    }
});
defineExpose({ start, running })
</script>
