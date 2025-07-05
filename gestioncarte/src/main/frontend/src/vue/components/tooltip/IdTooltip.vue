<template>
    <span class="ms-1 not-draggable id-tooltip" @click="setInClipboard">
        <template v-if="!!id">
            <Icon class="mt-1" name="information-circle-outline" />
            <Tooltip>{{id}}</Tooltip>
        </template>
        <Icon v-else class="mt-1" name="document-outline" />
    </span>
</template>
<script lang="ts" setup>
import Icon from "@components/Icon.vue";
import Tooltip from "@components/tooltip/Tooltip.vue"
import {useRaise} from "@/alert";

interface Props {
    id?: string | number;
}

const props = defineProps<Props>();
const raise = useRaise();

const setInClipboard = (e: Event) => {
    if (!props.id) {
        return;
    }

    navigator.permissions.query({name: "clipboard-write" as any}).then(result => {
        if ((result.state == "granted" || result.state == "prompt")) {
            navigator.clipboard.writeText(props.id?.toString() || '').then(() => raise.success("ID copié dans le presse-papier"));
        }
    });
    e.preventDefault();
    e.stopPropagation();
}
</script>

<style lang="scss" scoped>
.id-tooltip {
    position: relative;
    top: 2px;
    pointer-events: all !important;
}
</style>
