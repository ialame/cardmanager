<template>
    <ExtractionStatus :legends="legends" :keys="entry?.status" />
</template>

<script lang="ts" setup>
import ExtractionStatus from "@components/extraction/ExtractionStatus.vue";
import {ExtractedPokemonCardEntry, legends} from "@components/cards/pokemon/extracted/list";

interface Props {
    entry?: ExtractedPokemonCardEntry;
}

defineProps<Props>();

</script>
