<template>
    <div class="container mt-4">
        <FormRow>
            <Column>
                <YuGiOhSetSelect v-model="set" />
            </Column>
        </FormRow>
        <FormRow>
            <Column>
                <FormButton color="primary" @click="startExtraction()" :disabled="!set">Extraire</FormButton>
            </Column>
        </FormRow>
        <hr>
        <Row>
            <Column>
                <RouterLink class="fw-bold text-black" to="/cards/yugioh/extracted">{{extractedCardsCount}} cartes extraites</RouterLink>
            </Column>
            <Column>
                <div class="float-end">
                    <ClearExtractedCards class="me-2" />
                    <a :class="{'btn btn-secondary me-2': true, 'disabled': 0 === extractedCardsCount}" href="/api/cards/yugioh/extracted" :download="'extracted-yugioh-cards-' + getDateStr() + '.json'"><Icon src="/svg/download.svg" /></a>
                    <UploadButton title="Upload des cartes en json" @upload="uploadJson" />
                </div>
            </Column>
        </Row>
    </div>
    <hr v-if="running">
    <div v-show="running" class="d-flex justify-content-center container">
        <div class="w-50">
            <ProgressWatcher ref="watcher" endpoint="/api/cards/yugioh/extract/progress" @start="startProgress" @end="endProgress" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import {YuGiOhSetSelect} from "@components/cards/yugioh/set";
import {computed, ref} from "vue";
import {YuGiOhSetDTO} from "@/types";
import FormButton from "@components/form/FormButton.vue";
import rest from "@/rest";
import {useRaise} from "@/alert";
import Column from "@components/grid/Column.vue";
import FormRow from "@components/form/FormRow.vue";
import Row from "@components/grid/Row.vue";
import {ClearExtractedCards, extractedYuGiOhCardsService} from "@components/cards/yugioh/extracted/list";
import UploadButton from "@components/form/UploadButton.vue";
import Icon from "@components/Icon.vue";
import {getDateStr} from '@/retriever';
import ProgressWatcher from "@/progress/ProgressWatcher.vue";

const raise = useRaise();
const set = ref<YuGiOhSetDTO>();

const running = ref(false);
const watcher = ref<typeof ProgressWatcher>();
const startExtraction = async () => {
    if (!set.value) {
        raise.warn("Veuillez sélectionner une extension.");
        return;
    }
    await rest.post(`/api/cards/yugioh/set/${set.value?.id}/extract`);
    raise.success("Extraction lancée.");
    set.value = undefined;
    watcher.value?.start();
}
const extractedCardsCount = computed(() => extractedYuGiOhCardsService.all.value.length);
const uploadJson = async (content: ArrayBuffer) => {
    const decoder = new TextDecoder("utf-8");

    await extractedYuGiOhCardsService.addCards(JSON.parse(decoder.decode(content)));
    raise.success('Cartes uploadées avec succès');
}

const startProgress =() => {
    running.value = true;
}
const endProgress = () => {
    running.value = false;
    extractedYuGiOhCardsService.fetch();
    raise.success("Extraction terminée.");
}
</script>
