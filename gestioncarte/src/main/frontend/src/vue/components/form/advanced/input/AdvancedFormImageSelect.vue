<template>
    <AdvancedFormCollapse :label="label" :path="path">
        <template #label><slot name="label" /></template>
        <template #after-label>
            <template v-if="!readOnly">
                <UploadButton title="Upload d'image" accept=".png" @upload="addImage" class="ms-2 form-btn" />
                <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click.stop="reviewed = true">
                    <Icon name="checkmark-outline" />
                </FormButton>
            </template>
            <AdvancedFormFeedback class="ms-2" :validationResults="validationResults" />
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
        </template>
        <Row v-if="usedImages.length > 0" class="mt-2 advanced-form-image-select">
            <Column size="2" v-for="(image, index) in usedImages" :key="index" class="me-2" :class="{'selected': selectedImage === index}" @click="selectedImage = index">
                <div class="position-absolute saved-icon">
                    <Icon v-if="image.source === 'saved'" name="checkmark-outline" class="text-success icon-24" />
                    <Icon v-else-if="image.source === 'upload'" src="/svg/download.svg" class="v-flip text-secondary icon-24" />
                    <slot v-else name="source-icon" :image="image" />
                </div>
                <span class="helper" />
                <img :src="`data:image/png;base64,${image.base64Image}`" />
            </Column>
        </Row>
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import Column from '@components/grid/Column.vue';
import Row from '@components/grid/Row.vue';
import UploadButton from "@components/form/UploadButton.vue";
import {computed, nextTick, ref} from "vue";
import {Path} from "@/path";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import Icon from "@components/Icon.vue";
import {findIndex} from "lodash";
import FormButton from "@components/form/FormButton.vue";
import {useRaise} from "@/alert";
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import {encodeImage} from "@/image";
import {ExtractedImageDTO} from "@/types";

interface Props {
    path: Path;
    label?: string;
    images?: ExtractedImageDTO[];
}

const props = withDefaults(defineProps<Props>(), {
    label: '',
    images: () => []
});

const raise = useRaise();

const { value, readOnly, validationResults, reviewed, reviewable } = useAdvancedFormInput<any, ExtractedImageDTO>(() => props.path);

const uploadedImages = ref<ExtractedImageDTO[]>([]);
const usedImages = computed(() => [...props.images, ...uploadedImages.value]);
const selectedImage = computed({
    get: () => findIndex(usedImages.value, image => image.base64Image === value.value?.base64Image),
    set: (index: number) => {
        if (index === -1 || index >= usedImages.value.length) {
            value.value = undefined;
        } else {
            value.value = usedImages.value[index];
        }
    }
});

const addImage = (content: ArrayBuffer) => {
    const base64 = encodeImage(content);

    if (usedImages.value.some(image => image.base64Image === base64)) {
        raise.info('Cette image a déjà été ajoutée');
        return;
    }

    const image: ExtractedImageDTO = {
        base64Image: base64,
        source: 'upload'
    };

    uploadedImages.value.push(image);
    if (selectedImage.value === -1 || selectedImage.value >= usedImages.value.length) {
        nextTick(() =>  selectedImage.value = usedImages.value.length - 1);
    }
    raise.success("Image uploadée avec succès.");
};
</script>

<style lang="scss" scoped>
@import 'src/variables';

div.advanced-form-image-select>div {
    white-space: nowrap;
    text-align: center;
    cursor: pointer;

    &.selected {
        border: 3px solid $primary;
        border-radius: var(--bs-border-radius) !important;
    }
    .helper {
        display: inline-block;
        height: 100%;
        vertical-align: middle;
    }
    img {
        max-width: 100%;
        max-height: 100%;
    }
    .saved-icon:deep(ion-icon) {
        position: absolute;
        left: -10px;
        top: 2px;
    }
}
</style>
