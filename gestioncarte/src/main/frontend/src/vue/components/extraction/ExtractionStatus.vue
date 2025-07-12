<template>
    <div v-if="keys.length > 0" class="align-middle">
        <template v-for="(legend, index) in legends" :key="index">
            <div v-if="keys.includes(legend.key)" :title="legend.text" class="align-middle text-center d-inline me-1">
                <Icon class="icon-24" :class="getLegendColor(legend)" :name="getLegendName(legend)" :src="getLegendSrc(legend)" />
            </div>
        </template>
    </div>
</template>

<script lang="ts" setup>
import Icon from "@components/Icon.vue";
import {ExtractionStatusLegendType} from "@components/extraction/logic";
import {getLegendColor, getLegendName, getLegendSrc} from "@/types";

interface Props {
    legends: ExtractionStatusLegendType[];
    keys?: string[];
}

withDefaults(defineProps<Props>(),{
    legends: () => [],
    keys: () => []
});

</script>
