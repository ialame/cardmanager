<template>
    <div ref="drop" class="row mb-1 drop-row" :class="{ 'both': hasUs && hasJp }">
        <ReassociationCardLine v-if="hasUs" :entry="entry" localization="us" />
        <ReassociationCardLine v-if="hasJp" :entry="entry" localization="jp" />
    </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue';
import ReassociationCardLine from "./ReassociationCardLine.vue";
import {DragAndDropComposables} from "@/vue/composables/DragAndDropComposables";
import {ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";

interface Props {
    entry: ExtractedPokemonCardEntry;
}
interface Emits {
    (e: 'reassociate', us: string, jp: string): void;
}

const props = defineProps<Props>();
const emits = defineEmits<Emits>();

const drop = ref<HTMLDivElement>();
 DragAndDropComposables.useDrop("reassociation", drop, (p: Props) => {
    if (p.entry.extractedCard.id !== props.entry.extractedCard.id) {
        emits('reassociate', p.entry.extractedCard.id, props.entry.extractedCard.id);
    }
});

const hasUs = computed(() => props.entry.extractedCard.card.translations.us !== undefined);
const hasJp = computed(() => props.entry.extractedCard.card.translations.jp !== undefined);
</script>

<style lang="scss" scoped>
@import 'src/variables.scss';

.drop-row {
    position: relative;
    border: transparent 0.5rem solid;
    border-radius: $pca-border-radius;
    &.both {
        border-color: $primary;
    }
}
</style>
