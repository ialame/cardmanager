<template>
    <div v-if="!error || showOnError" class="extracted-image me-2 mb-2">
        <span v-if="width > 0 && height > 0" class="image-size">{{ width }}x{{ height }}</span>

        <img v-if="image.base64Image" :src="`data:image/png;base64,${image.base64Image}`" @load="e => onLoad(e.target as HTMLImageElement)" @error="onError" @click.stop="$emit('click')" />
        <img v-else-if="image.url" :src="image.url" @load="e => onLoad(e.target as HTMLImageElement)" @error="onError" @click.stop="$emit('click')" />
        <span v-else-if="error" class="extracted-image-error" @click.stop="$emit('click')">&nbsp;</span>

        <FormButton color="secondary" class="round-btn-sm image-info-button me-2 mt-1" @click.stop="modal?.show()">
            <Icon class="icon-24" name="search-outline" />
        </FormButton>
        <Modal ref="modal" size="xxl">
            <div class="container">
                <Row>
                    <Column>
                        <img v-if="image.base64Image" :src="`data:image/png;base64,${image.base64Image}`" />
                        <img v-else-if="image.url" :src="image.url" />
                        <span v-else-if="error" class="extracted-image-error">&nbsp;</span>
                    </Column>
                    <Column>
                        <Row>
                            <Column size="2">Taille : </Column>
                            <Column>{{ width }}x{{ height }}</Column>
                        </Row>
                        <Row>
                            <Column size="2">Langue : </Column>
                            <Column><Flag :lang="image.localization" /></Column>
                        </Row>
                        <Row>
                            <Column size="2">Source : </Column>
                            <Column>{{ image.source }}</Column>
                        </Row>
                        <Row>
                            <Column size="2">URL : </Column>
                            <Column><a :href="image.url" target="_blank">{{ image.url }}</a></Column>
                        </Row>
                    </Column>
                </Row>
            </div>
        </Modal>
    </div>
</template>

<script setup lang="ts">
import {ExtractedImageDTO} from "@/types";
import {ref, watch} from "vue";
import Modal from "@components/modal/Modal.vue";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {Flag} from "@/localization";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";

interface Props {
    image: ExtractedImageDTO;
    showOnError?: boolean;
}

interface Emits {
    (e: 'error'): void;
    (e: 'click'): void;
}

withDefaults(defineProps<Props>(), {
    showOnError: false
});

const emit = defineEmits<Emits>();

const modal = ref<InstanceType<typeof Modal>>();

const width = ref<number>(0);
const height = ref<number>(0);
const error = ref<boolean>(false);

const onLoad = (target: HTMLImageElement) => {
    width.value = target?.naturalWidth ?? 0;
    height.value = target?.naturalHeight ?? 0;
    error.value = width.value === 0 || height.value === 0;
}
const onError = () => {
    error.value = true;
}
watch(error, e => {
    if (e) {
        emit('error');
    }
});
</script>

<style lang="scss" scoped>
@import 'src/variables';

.extracted-image {
    position: relative;
    padding: 0.1rem;

    >img {
        width: 100%;
        height: 100%;
    }
}

.extracted-image-error {
    display: block;
    min-width: 100%;
    min-height: 100%;
    border: 1px dashed $secondary;
    border-radius: 10px;
    background-color: rgba(lightgray, 0.25);
}

.image-size {
    font-size: 0.8rem;
    background-color: rgba(lightgray, 0.5);
    padding-left: 0.2rem;
    padding-right: 0.2rem;
    position: absolute;
}
.image-info-button {
    position: absolute;
    right: 0;
}
</style>
