<template>
    <span v-if="localization && localizations[localization] !== undefined" class="h5" ref="span">
        <Flag :lang="localization" class="icon-24" :class="flagClass" /> {{localizations[localization].name}}
    </span>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {Flag, LocalizationCode, localizations} from "@/localization";

interface Props {
    localization: LocalizationCode;
    flagClass?: string | Record<string, boolean>;
}

withDefaults(defineProps<Props>(), {
    flagClass: ""
});
const span = ref<HTMLSpanElement>();

const scrollIntoView = () => span.value?.scrollIntoView({ behavior: 'smooth' });

defineExpose({scrollIntoView});
</script>
