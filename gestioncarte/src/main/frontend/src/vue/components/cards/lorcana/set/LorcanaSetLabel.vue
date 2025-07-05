<template>
    <div class="d-flex flex-row overflow-hidden">
        <Flag v-if="localization" class="mt-2 me-1" :lang="localization" />
        {{ name }}
        <IdTooltip :id="computedSet?.id" />
    </div>
</template>

<script lang="ts" setup>
import {LorcanaSetDTO, LorcanaSetTranslationDTO} from "@/types";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag, getTranslationField} from "@/localization";
import {getSetName} from "@components/cards/lorcana/set/logic";
import {computedAsync} from "@vueuse/core";
import {lorcanaSetService} from "@components/cards/lorcana/set/service";
import {isString} from "lodash";

interface Props {
    set?: LorcanaSetDTO | string;
}

const props = defineProps<Props>();

const computedSet = computedAsync(() => isString(props.set) ? lorcanaSetService.get(props.set) : props.set);
const name = computed(() => getSetName(computedSet.value) + (computedSet.value?.code ? ` (${computedSet.value?.code})` : ''));
const localization = computed(() => getTranslationField<LorcanaSetTranslationDTO>(computedSet.value?.translations)?.[0]);

</script>

<style lang="scss" scoped>
.set-icon {
    width: 30px;
    margin-right: 0.5rem;
    text-align: center;

    :deep(object) {
        height: 16px;
    }
}
</style>
