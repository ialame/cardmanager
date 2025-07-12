<template>
    <Flag v-if="localization" :lang="localization" /> {{ name }} <IdTooltip :id="computedVersion?.id" />
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag} from "@/localization";
import {PromoCardVersionDTO} from "@/types";
import {computed} from "vue";
import {computedAsync} from "@vueuse/core";
import {promoCardVersionService} from "@components/cards/promo";
import {isString} from "lodash";


interface Props {
    version?: PromoCardVersionDTO | string;
}

const props = defineProps<Props>();

const computedVersion = computedAsync(async () => {
    if (isString(props.version)) {
        return await promoCardVersionService.get(props.version);
    }
    return props.version;
});

const name = computed(() => computedVersion.value?.name || computedVersion.value?.translations?.us?.name || computedVersion.value?.translations?.jp?.name || computedVersion.value?.translations?.cn?.name || '!!! VERSION SANS NOM !!!');
const localization = computed(() => {
    if (computedVersion.value?.translations?.us) {
        return 'us';
    } else if (computedVersion.value?.translations?.jp) {
        return 'jp';
    } else if (computedVersion.value?.translations?.cn) {
        return 'cn';
    }
    return undefined;
});
</script>
