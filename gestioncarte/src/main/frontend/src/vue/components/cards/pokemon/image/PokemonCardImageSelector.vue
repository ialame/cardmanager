<template>
    <Row class="mb-3">
        <Column size="3">
            <SingleLineCard v-if="card" :card="card" localization="us" />
            <ExtractedImage v-if="current" class="current mt-2" :class="{ 'selected': selectedVModel === 0 }" :image="current" @click="selectedVModel = 0" />
        </Column>
        <Column class="images d-inline-flex flex-wrap pe-0">
            <template v-for="(image, index) in extractedImages" :key="index">
                <ExtractedImage v-if="image !== current" :class="{ 'selected': selectedVModel === index }" :image="image" @click="selectedVModel = index" />
            </template>
            <div ref="dropZone" @dragenter.prevent @dragover.prevent @dragleave.prevent>
                <span class="image-drop">
                    <Icon src="/svg/download.svg" class="v-flip text-secondary icon-32" />
                </span>
            </div>
        </Column>
    </Row>
</template>

<script setup lang="ts">
import {ExtractedImageDTO, PokemonCardDTO} from "@/types";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import SingleLineCard from "@components/cards/pokemon/SingleLineCard.vue";
import ExtractedImage from "@components/images/ExtractedImage.vue";
import {useDropZone, useVModel} from "@vueuse/core";
import {computed, ref} from "vue";
import Icon from "@components/Icon.vue";

interface Props {
    card: PokemonCardDTO;
    extractedImages: ExtractedImageDTO[];
    selected: number;
}

interface Emit {
    (event: 'update:selected', value: number): void;
    (event: 'upload', value: File): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emit>();

const selectedVModel = useVModel(props, 'selected', emit);

const current = computed(() => {
    const image = props.extractedImages[0];

    return image?.source === 'current' ? image : null;
});


const dropZone = ref<HTMLElement>();

useDropZone(dropZone, {
  onDrop: files => {
    console.log('onDrop Fired!', files); // Log pour voir si la fonction est appelée
    const file = files?.[0];

    if (file) {
      console.log('File found:', file.name, file.type); // Log le nom et le type
      // Validez le type ici si vous avez supprimé dataTypes
      if (file.type === 'image/png' || file.type === 'image/jpeg') { // Exemple d'acceptation
        emit('upload', file);
      } else {
        console.warn('Invalid file type dropped:', file.type);
        // Peut-être afficher une erreur à l'utilisateur
      }
    } else {
      console.log('No file found in drop event.');
    }
  },
  // dataTypes: ['image/png'] // Commenté pour le test
});

// Ajouter aussi pour vérifier que la référence est correcte
import { onMounted } from 'vue';
onMounted(() => {
  console.log('Drop zone element:', dropZone.value);
});
</script>

<style lang="scss" scoped>
@import 'src/variables';

@mixin selected {
    border: 5px solid $primary;
    border-radius: 10px;
}

.images {
    &:deep(>*) {
        width: 252px;
        height: 356px;
    }
    &:deep(.selected) {
        @include selected;
    }
    .image-drop {
        display: block;
        min-width: 100%;
        min-height: 100%;
        border: 1px dashed $secondary;
        border-radius: 10px;
        background-color: rgba(lightgray, 0.25);
        text-align: center;
        padding-top: 50%;
    }
}

.current {
    width: 126px;
    height: 178px;
    margin-left: auto;

    &.selected {
        @include selected;
    }
}
</style>
