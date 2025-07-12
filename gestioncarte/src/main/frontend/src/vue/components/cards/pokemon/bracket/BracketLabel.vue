<template>
    <Flag v-if="localization && !hideFlag" class="mt-2 me-1" :lang="localization" />
    {{hideFlag ? `[${name}]` : name}}
    <IdTooltip :id="bracket?.id" />
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {BracketDTO} from "@/types";
import {computed} from "vue";
import {Flag} from "@/localization";
import {isEmpty} from "lodash";

interface Props {
    bracket?: BracketDTO;
    hideFlag?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    hideFlag: false
});


const name = computed(() => props.bracket?.name ?? (isEmpty(props.bracket?.id) ? 'Nouveau crochet' : '!!! CROCHET SANS NOM !!!'));
const localization = computed(() => {
    if (props.bracket?.localization) {
        return props.bracket?.localization;
    } else if (props.bracket?.translations?.us) {
        return 'us';
    } else if (props.bracket?.translations?.jp) {
        return 'jp';
    } else if (props.bracket?.translations?.cn) {
        return 'cn';
    }
    return undefined;
});
</script>
