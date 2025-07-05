<template>
    <div class="d-flex flex-row overflow-hidden">
        <Flag v-if="localization" class="mt-2 me-1" :lang="localization" />
        {{ name }}
        <IdTooltip :id="set?.id" />
    </div>
</template>

<script lang="ts" setup>
import {YuGiOhSetDTO, YuGiOhSetTranslationDTO} from "@/types";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag, getTranslationField} from "@/localization";
import {isString} from "lodash";
import {yugiohSetService} from "@components/cards/yugioh/set/service";
import {computedAsync} from "@vueuse/core";

interface Props {
    set?: YuGiOhSetDTO | string;
}

const props = defineProps<Props>();

const computedSet = computedAsync(() => isString(props.set) ? yugiohSetService.get(props.set) : props.set);
const name = computed(() => {
    if (!computedSet.value) {
        return '!!! EXTENSION SANS NOM !!!';
    }

    const t = getTranslationField<YuGiOhSetTranslationDTO, "name">(computedSet.value.translations, 'name');

    if (t) {
        const code = computedSet.value?.translations?.[t[0]]?.code;

        if (code) {
            return `${t[1]} (${code})`;
        }
        return t[1];
    }
    return '!!! EXTENSION SANS NOM !!!';
});
const localization = computed(() => getTranslationField<YuGiOhSetTranslationDTO>(computedSet.value?.translations)?.[0]);

</script>
