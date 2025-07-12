<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <PokemonSetSearch v-model="set" @update:localization-filter="handleLocalizationChange" >
        <FormRow>
            <Column>
                <div class="d-flex flex-row float-end">
                    <FormButton color="primary" @click="extract">Extraire</FormButton>
                </div>
            </Column>
        </FormRow>
    </PokemonSetSearch>
    <hr />
    <template v-if="set && totalExtractedImages > 0">
        <div class="container mt-2">
            <div class="d-flex justify-content-center">
                <div class="w-50 ms-auto me-auto">
                    <ProgressBar :value="saved > -1 ? saved : extractedImages.length" :max="totalExtractedImages" :label="saved > -1 ? `Enregistrement des images` : `Extraction des images`" />
                </div>
                <div v-if="extractedImages.length === totalExtractedImages" class="ms-auto">
                    <FormButton color="primary" @Click="save">Enregistrer</FormButton>
                </div>
            </div>
        </div>
        <hr />
    </template>
    <div class="full-screen-container mt-2">
        <template v-for="image in extractedImages" :key="image.card.id">
            <PokemonCardImageSelector :card="image.card" :extractedImages="image.images" v-model:selected="image.selected" @upload="f => onUpload(image, f)" />
        </template>
    </div>
</template>

<script setup lang="ts">
import {ScrollToTop, SideButtons} from "@components/side";
import {PokemonSetSearch} from "@components/cards/pokemon/set";
import {ref} from "vue";
import {ExtractedImageDTO, ExtractedPokemonImagesDTO, Page, PokemonCardDTO, PokemonSetDTO} from '@/types';
import rest from "@/rest";
import PokemonCardImageSelector from "@components/cards/pokemon/image/PokemonCardImageSelector.vue";
import {pokemonCardService} from "@components/cards/pokemon/service";
import log from "loglevel";
import {ProgressBar} from "@/progress";
import {usePageTitle} from "@/vue/composables/PageComposables";
import FormButton from "@components/form/FormButton.vue";
import {useRaise} from "@/alert";
import Column from "@components/grid/Column.vue";
import FormRow from "@components/form/FormRow.vue";
import {triggerRef} from "vue-demi";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import {useSound} from "@vueuse/sound";
import pingSfx from "@/../assets/sound/ping.mp3";
import {LocalizationCode} from "@/localization";

type CardAndImages = {
    card: PokemonCardDTO;
    images: ExtractedImageDTO[];
    selected: number;
}

usePageTitle("Pokémon - Ajout d'images");

const raise = useRaise();
const { play } = useSound(pingSfx);

const set = ref<PokemonSetDTO>();

const extractedImages = ref<CardAndImages[]>([]);
const totalExtractedImages = ref<number>(0);

const currentLocale = ref('');

function handleLocalizationChange(locale:LocalizationCode) {
  currentLocale.value = locale;
  console.log('Locale sélectionnée:', locale);
}

const addImages = async (images: ExtractedPokemonImagesDTO[]) => {
    const cards = await Promise.all(images.map(async ({cardId, images}) => ({
        card: await pokemonCardService.get(cardId),
        images,
        selected: 0
    })));

    extractedImages.value.push(...cards);
    extractedImages.value.sort((a, b) => {
        const aIdPrim = a.card.idPrim;
        const bIdPrim = b.card.idPrim;

        return aIdPrim && bIdPrim ? aIdPrim.localeCompare(bIdPrim) : 0;
    });
    triggerRef(extractedImages);
}

const extract = async () => {
    const id = set.value?.id;

    if (!id) {
        return;
    }

    extractedImages.value = [];

    //const page: Page<ExtractedPokemonImagesDTO> = await rest.get(`/api/cards/pokemon/images/extract/sets/${id}`)

  // Récupère la première page
  let initialPage;
  try {
    const response = await rest.get(`/api/cards/pokemon/images/extract/sets/${id}`, {
      params: {
        locale: currentLocale.value,
        page: 0,
        size: 25
      }
    });
    console.log('Initial page response:', response);
    initialPage = response;
  } catch (error) {
    console.error('Error fetching initial page:', error);
    throw error; // Ou gérer l'erreur autrement
  }

  totalExtractedImages.value = initialPage.totalElements;
  await addImages(initialPage.content);

  console.log('Total pages:', initialPage.totalPages);
  if (initialPage.totalPages > 1) {
    await Promise.all(
        Array.from({ length: initialPage.totalPages - 1 }, (_, i) => i + 1).map(async pageNumber => {
          const params = {
            locale: encodeURIComponent(currentLocale.value),
            page: pageNumber,
            size: 25
          };
          const url = `/api/cards/pokemon/images/extract/sets/${encodeURIComponent(id)}`;
          console.log('Fetching URL:', url, 'with params:', params);
          const pagedResult = await rest.get(url, { params });
          return addImages(pagedResult.content);
        })
    );
  } else {
    console.log('No additional pages to fetch (totalPages <= 1)');
  }
    log.info("Extracted images: ", extractedImages.value);
    play();
}

const onUpload = (image: CardAndImages, file: File) => {
    const reader = new FileReader();

    reader.onload = async () => {
        let base64 = reader.result as string;

        if (base64.startsWith('data:')) {
            base64 = base64.split(',')[1] ?? base64;
        }
        const extracted: ExtractedImageDTO = {
            localization: image.card.translations.us?.available ? 'us' : 'jp',
            internal: true,
            base64Image: base64,
            source: 'upload'
        };

        image.images.push(extracted);
        image.selected = image.images.length - 1;
        triggerRef(extractedImages);
    }

    reader.readAsDataURL(file);

}

const saved = ref(-1);

const {save} = SaveComposables.useLockedSaveAsync(async () => {
    saved.value = 0;
    await Promise.all(extractedImages.value.map(async ({card, images, selected}) => {
        if (images.length === 0 || selected < 0 || selected >= images.length) {
            return;
        }

        await rest.put(`/api/cards/pokemon/${card.id}/images`, { data: images[selected] });
        saved.value++;
    }));
    raise.success("Images enregistrées");
    saved.value = -1;
    set.value = undefined;
    extractedImages.value = [];
    totalExtractedImages.value = 0;
    play();
});
</script>
