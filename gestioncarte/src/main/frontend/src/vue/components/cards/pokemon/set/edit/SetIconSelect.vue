<template>
    <FormRow v-if="shortName">
        <AdvancedFormImageSelect path="icon" label="Icon" :images="images" class="set-icon-select">
            <template #source-icon="{image}">
                <BulbapediaIcon v-if="image.source === 'bulbapedia'" class="icon-24" />
            </template>
        </AdvancedFormImageSelect>
    </FormRow>
</template>

<script lang="ts" setup>
import FormRow from '@components/form/FormRow.vue';
import {computed} from "vue";
import {RestComposables} from "@/vue/composables/RestComposables";
import rest from "@/rest";
import {AdvancedFormImageSelect, useAdvancedFormInput} from "@components/form/advanced";
import {EditedPokemonSet} from "./logic";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";
import {getSetIcon} from "@components/cards/pokemon/set";
import {ExtractedImageDTO} from "@/types";


const {context} = useAdvancedFormInput<EditedPokemonSet, ExtractedImageDTO>('icon');

const shortName = computed(() => context.value.data.shortName);
const { state: images, refresh } = RestComposables.useRestComposable<ExtractedImageDTO[], [], true>(async () => {
    if (!shortName.value) {
        return [];
    }

    const images: ExtractedImageDTO[] = [];
    const image = await getSetIcon(shortName.value);

    if (image) {
        images.push(image);
    }
    if (context.value.data.expansionsBulbapedia) {
        for (const i of await rest.post('/api/cards/pokemon/bulbapedia/sets/icon/search', {data: context.value.data.expansionsBulbapedia.list}) as ExtractedImageDTO[]) {
            images.push(i);
        }
    }
    return images;
}, [], { watch: [() => shortName.value], fullState: true });

defineExpose({ refresh })
</script>

<style lang="scss" scoped>
.set-icon-select:deep(div.advanced-form-image-select>div) {
    width: 100px;
    height: 100px;
}
</style>
