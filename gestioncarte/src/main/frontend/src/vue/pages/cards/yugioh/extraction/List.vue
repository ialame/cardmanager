<template>
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
                <YuGiOhSetLabel :set="setId" />
            </template>
            <table class="table table-striped mt-4">
                <thead>
                    <tr>
                        <th scope="col"></th>
                        <th scope="col">Numero</th>
                        <th scope="col">Nom de la carte</th>
                        <th scope="col">Rareté</th>
                        <th scope="col" class="min-w-100">Langues</th>
                        <th scope="col" class="min-w-150"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="entry in sortEntries(entryGroup)" :key="entry.extractedCard.id">
                        <td>
                            <SelectionCheckbox :entry="entry" />
                        </td>
                        <td>{{getYuGiOhCardNumber(entry.extractedCard.card)}}</td>
                        <td><a class="text-dark fw-bold c-pointer" @click="nextEditCardWorkflow(entry.extractedCard.id)">{{getYuGiOhCardName(entry.extractedCard.card)}}</a></td>
                        <td>{{getYuGiOhCardRarity(entry.extractedCard.card)}}</td>
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
        <YuGiOhCardEditForm v-if="editedCard" v-model="editedCard" :mergeSources="editedCardMergeSources" @save="saveEditedCard" />
    </Modal>
</template>

<script lang="ts" setup>
import {Flag} from "@/localization";
import {ExtractedYuGiOhCardDTO, YuGiOhCardDTO} from "@/types";
import Modal from "@components/modal/Modal.vue";
import {computed, ref} from "vue";
import {YuGiOhCardEditForm} from "@components/cards/yugioh/edit";
import {cloneDeep, groupBy, sortBy} from "lodash";
import {
    ClearExtractedCards,
    ExtractedYuGiOhCardEntry,
    extractedYuGiOhCardsService,
    useExtractedYuGiOhCardEntries
} from "@components/cards/yugioh/extracted/list";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import rest from "@/rest";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import {downloadData, getDateStr} from "@/retriever";
import {ComposablesHelper} from "@/vue/composables/ComposablesHelper";
import {WorkflowComposables} from "@/vue/composables/WorkflowComposables";
import {ProgressBar} from '@/progress';
import {getYuGiOhCardName, getYuGiOhCardNumber, getYuGiOhCardRarity} from "@components/cards/yugioh";
import Collapse from "@components/collapse/Collapse.vue";
import YuGiOhSetLabel from "@components/cards/yugioh/set/YuGiOhSetLabel.vue";
import {useSelectedCards} from "@components/extraction";
import {SelectionCheckbox} from "@/selection";
import Checkbox from "@components/form/Checkbox.vue";
import {ConsolidationSource, useProvideConsolidationSources} from "@components/form/advanced";

const cards = extractedYuGiOhCardsService.all;
const { entries, setEntry, ignoreCard } = useExtractedYuGiOhCardEntries();

const groupedEntries = computed(() => groupBy(entries.value, e => e.extractedCard.card.setIds[0]));

const sortEntries = (entryGroup: ExtractedYuGiOhCardEntry[]) => sortBy(entryGroup, e => getYuGiOhCardNumber(e.extractedCard.card));

const editCardWorkflow = ComposablesHelper.unwrap(WorkflowComposables.useWorkflow(cards, {isValid: () => true /* TODO add validity check */}), 'next', 'isValid', 'reset');
const editCardModal = ref<typeof Modal>();
const editedCard = ref<YuGiOhCardDTO>();
const editedCardMergeSources = ref<YuGiOhCardDTO[]>();
const consolidationSources = ref<ConsolidationSource<YuGiOhCardDTO>[]>();

const nextEditCardWorkflow = (id?: string) => {
    const index = id !== undefined ? cards.value.findIndex(c => c.id === id) : undefined;
    const i = editCardWorkflow.value.next(index);

    if (i !== undefined) {
        editedCard.value = cloneDeep(editCardWorkflow.value.current?.card);
        editedCardMergeSources.value = cloneDeep(editCardWorkflow.value.current?.savedCards);
        consolidationSources.value = editCardWorkflow.value.current?.sources;
        editCardModal.value?.show();
    } else {
        editedCard.value = undefined;
        editedCardMergeSources.value = undefined;
        consolidationSources.value = [];
        editCardModal.value?.hide();
    }
}

useProvideConsolidationSources(() => consolidationSources.value ?? []);

const saveEditedCard = () => {
    if (editCardWorkflow.value.index === undefined || editedCard.value === undefined || editedCardMergeSources.value === undefined) {
        return;
    }

    const entry = cloneDeep(entries.value[editCardWorkflow.value.index]);

    entry.extractedCard.card = cloneDeep(editedCard.value);
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
        await Promise.all(extractedYuGiOhCardsService.all.value.map(async (c: ExtractedYuGiOhCardDTO) => {
            if (c.savedCards.length) {
                await rest.put(`/api/cards/yugioh/${c.savedCards.map(a => a.id).join(',')}/merge`, {data: c.card});
            } else {
                await rest.put('/api/cards/yugioh', {data: c.card});
            }

            await rest.put('/api/cards/yugioh/extracted/history', {data: c.rawExtractedCard});
        }));
        await extractedYuGiOhCardsService.clear();

        entries.value = [];
        raise.success('Cartes enregistrées avec succès');
    } catch {
        raise.error('Une erreur est survenue lors de l\'enregistrement des cartes');
    }
});

const { selected, isSelected, revert, removeSelected, selectAll } = useSelectedCards(entries, e => e.id, ignoreCard);

const downloadJson = () => downloadData("extracted-yugioh-cards-" + getDateStr() + ".json", cards.value);
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
