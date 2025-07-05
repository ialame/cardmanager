<template>
    <SideButtons>
        <Legend :values="legends" />
        <ScrollToTop />
    </SideButtons>
    <div class="container mt-4">
        <div class="d-flex justify-content-between">
            <div>
                <ClearExtractedCards />
            </div>
            <div class="w-50">
                <ProgressBar label="Cartes à verifier" :value="editCardWorkflow.progress" :max="editCardWorkflow.total" />
            </div>
            <div class="d-flex flex-column">
                <FormButton color="primary" title="Demarer le workflow" @Click="nextEditCardWorkflow" class="mb-2" :disabled="editCardWorkflow.complete">Demarer le workflow</FormButton>
                <FormButton color="primary" title="Enregistrer les cartes" @click="saveCards" :disabled="!editCardWorkflow.complete" loading>Enregistrer</FormButton>
            </div>
        </div>
    </div>
    <hr>
    <div class="container">
        <Row>
            <Column size="sm">
                <template v-if="selected && selected.length > 0">
                    <FormButton class="me-2 mt-1 round-btn-sm" color="danger" title="Enlever les cartes séléctioné" @click="removeSelected">
                        <Icon name="trash" />
                    </FormButton>
                    <FormButton class="me-2 mt-1" color="secondary" title="Inverser la sélection" @click="revert">Inverser la sélection</FormButton>
                </template>
            </Column>
            <Column size="sm">
                <div class="float-end">
                    <FormButton color="secondary" class="me-2" title="Telecharger le resultat en json" @click="downloadJson()"><Icon src="/svg/download.svg" /></FormButton>
                </div>
            </Column>
        </Row>
    </div>
    <hr>
    <div class="full-screen-container mt-2" v-for="(entryGroup, setId) in groupedEntries" :key="setId">
        <Collapse :open="false" class="set-collapse">
            <template #label>
                <Checkbox class="ms-1" :modelValue="entryGroup.every(e => isSelected(e))" @update:modelValue="v => selectAll(entryGroup, v)" :slider="false" :indeterminate="entryGroup.some(e => isSelected(e)) && entryGroup.some(e => !isSelected(e))" />
                <OnePieceSetLabel :set="setId" />
            </template>
            <table class="table table-striped mt-4">
                <thead>
                    <tr>
                        <th scope="col"></th>
                        <th scope="col">Statut</th>
                        <th scope="col">Numero</th>
                        <th scope="col">Nom de la carte</th>
                        <th scope="col">Rareté</th>
                        <th scope="col" v-if="showPromos">Promo</th>
                        <th scope="col" class="min-w-100">Langues</th>
                        <th scope="col" class="min-w-150"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="entry in sortEntries(entryGroup)" :key="entry.extractedCard.id">
                        <td>
                            <SelectionCheckbox :entry="entry" />
                        </td>
                        <td>
                            <ExtractionStatus :legends="legends" :keys="entry?.status" />
                        </td>
                        <td>{{entry.extractedCard.card.number}}</td>
                        <td><a class="text-dark fw-bold c-pointer" @click="nextEditCardWorkflow(entry.extractedCard.id)">{{getOnePieceCardName(entry.extractedCard.card)}}</a></td>
                        <td>{{entry.extractedCard.card.rarity}}</td>
                        <td v-if="showPromos">
                            <HoverDataList :value="entry.extractedCard.card.promos" #default="{ value: promo }">{{ promo.name }}</HoverDataList>
                        </td>
                        <td>
                            <Flag v-for="localization in entry.extractedCard.card.translations" :key="localization" :lang="localization.localization" class="me-1" />
                        </td>
                        <td>
                            <div class="float-end">
                                <FormButton color="link" title="Enlever la carte" @click="ignoreCard(entry)" class="me-2 p-0 no-focus">
                                    <Icon name="trash" class="text-danger" />
                                </FormButton>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </Collapse>
    </div>
    <Modal ref="editCardModal" size="xxl">
        <OnePieceCardEditForm
            v-if="editedCard"
            v-model="editedCard"
            v-model:mergeSources="editedCardMergeSources"
            v-model:reviewedPaths="reviewedPaths"
            v-model:optionalPaths="optionalPaths"
            @save="saveEditedCard"
        />
    </Modal>
</template>

<script lang="ts" setup>
import {Flag} from "@/localization";
import {ExtractedOnePieceCardDTO, OnePieceCardDTO} from "@/types";
import Modal from "@components/modal/Modal.vue";
import {computed, onMounted, ref} from "vue";
import {cloneDeep, groupBy, sortBy} from "lodash";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import rest from "@/rest";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import {downloadData, getDateStr} from "@/retriever";
import {ProgressBar} from '@/progress';
import Collapse from "@components/collapse/Collapse.vue";
import {useSelectedCards} from "@components/extraction";
import {SelectionCheckbox} from "@/selection";
import Checkbox from "@components/form/Checkbox.vue";
import {
    ExtractedOnePieceCardEntry,
    useExtractedOnePieceCardEntries
} from "@components/cards/onepiece/extraction/list/ExtractedOnePieceCardEntry";
import {ClearExtractedCards, extractedOnePieceCardsService, legends} from "@components/cards/onepiece/extraction/list";
import {getOnePieceCardName, useOnePieceTitle} from "@components/cards/onepiece/logic";
import OnePieceSetLabel from "@components/cards/onepiece/set/OnePieceSetLabel.vue";
import OnePieceCardEditForm from "@components/cards/onepiece/edit/OnePieceCardEditForm.vue";
import {useRaise} from "@/alert";
import {Legend, ScrollToTop, SideButtons} from "@components/side";
import ExtractionStatus from "@components/extraction/ExtractionStatus.vue";
import HoverDataList from "@components/table/HoverDataList.vue";
import {Path} from "@/path";

useOnePieceTitle("Ajout de cartes");

const cards = extractedOnePieceCardsService.all;
const { entries, setEntry, ignoreCard, editCardWorkflow } = useExtractedOnePieceCardEntries();

const groupedEntries = computed(() => groupBy(entries.value, e => e.extractedCard.card.setIds[0]));

const showPromos = computed<boolean>(() => !!cards.value?.some(c => c.card.promos?.length > 0));

const sortEntries = (entryGroup: ExtractedOnePieceCardEntry[]) => sortBy(entryGroup, e => e.extractedCard.card.number);

const editCardModal = ref<typeof Modal>();
const editedCard = ref<OnePieceCardDTO>();
const optionalPaths = ref<Path[]>([]);
const reviewedPaths = ref<Path[]>([]);
const editedCardMergeSources = ref<OnePieceCardDTO[]>();
const raise = useRaise();

const nextEditCardWorkflow = (id?: string) => {
    const index = id !== undefined ? entries.value.findIndex(c => c.extractedCard.id === id) : undefined;
    const i = editCardWorkflow.value.next(index);

    if (i !== undefined) {
        editedCard.value = cloneDeep(editCardWorkflow.value.current?.extractedCard?.card);
        optionalPaths.value = cloneDeep(editCardWorkflow.value.current?.optionalPaths) ?? [];
        reviewedPaths.value = cloneDeep(editCardWorkflow.value.current?.reviewedPaths) ?? [];
        editedCardMergeSources.value = cloneDeep(editCardWorkflow.value.current?.extractedCard?.savedCards);
        editCardModal.value?.show();
    } else {
        editedCard.value = undefined;
        editedCardMergeSources.value = undefined;
        editCardModal.value?.hide();
    }
}
const saveEditedCard = () => {
    if (editCardWorkflow.value.index === undefined || editedCard.value === undefined || editedCardMergeSources.value === undefined) {
        return;
    }

    const entry = cloneDeep(entries.value[editCardWorkflow.value.index]);

    entry.extractedCard.card = cloneDeep(editedCard.value);
    entry.extractedCard.reviewed = true;
    entry.reviewedPaths = cloneDeep(reviewedPaths.value);
    entry.optionalPaths = cloneDeep(optionalPaths.value);
    entry.extractedCard.savedCards = cloneDeep(editedCardMergeSources.value)
    entry.status = [];
    entry.validationResults = {};
    setEntry(entry);
    nextEditCardWorkflow();
}
const {save: saveCards} = SaveComposables.useLockedSaveAsync(async () => {
    if (!editCardWorkflow.value.complete) {
        raise.warn(`Il y a encore ${entries.value.length - editCardWorkflow.value.progress} cartes non valides.`);
        return;
    }
    try {
        await Promise.all(extractedOnePieceCardsService.all.value.map(async (c: ExtractedOnePieceCardDTO) => {
            if (c.savedCards.length) {
                await rest.put(`/api/cards/onepiece/${c.savedCards.map(a => a.id).join(',')}/merge`, {data: c.card});
            } else {
                await rest.put('/api/cards/onepiece', {data: c.card});
            }

            await rest.put('/api/cards/onepiece/extracted/history', {data: c.rawExtractedCard});
        }));
        await extractedOnePieceCardsService.clear();

        entries.value = [];
        raise.success('Cartes enregistrées avec succès');
    } catch {
        raise.error('Une erreur est survenue lors de l\'enregistrement des cartes');
    }
});

const { selected, isSelected, revert, removeSelected, selectAll } = useSelectedCards(entries, e => e.id, ignoreCard);

const downloadJson = () => downloadData("extracted-onepiece-cards-" + getDateStr() + ".json", cards.value);
onMounted(() => extractedOnePieceCardsService.fetch());
</script>

<style lang="scss" scoped>
.set-collapse:deep(>div>.collapse-button) {
    display: flex;
    flex-direction: row;

    &:before {
        margin-top: 0.25rem;
    }
}
</style>
