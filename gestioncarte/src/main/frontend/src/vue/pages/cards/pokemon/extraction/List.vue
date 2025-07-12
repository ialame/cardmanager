<template>
    <Loading :ready="ready">
        <SideButtons>
            <Legend :values="legends" />
            <ScrollToTop />
        </SideButtons>
        <template v-if="!isEmpty(entries) && !isEmpty(sets)">
            <div class="container mt-2">
                <div class="d-flex justify-content-between">
                    <div>
                        <ClearExtractedCards class="mb-2" @clear="clearCards" />
                    </div>
                    <div class="w-50">
                        <ProgressBar v-if="reassociationWorkflow.total > 0" label="Reassociations à verifier" :value="reassociationWorkflow.progress" :max="reassociationWorkflow.total" />
                        <ProgressBar label="Cartes à verifier" :value="cardsWorkflow.progress" :max="cardsWorkflow.total" />
                        <div class="d-flex justify-content-center">
                            <TabButton v-for="localization in availableLocalizations" :key="localization" :name="localization" v-model="mainLocalization" :title="localizations[localization]?.name" class="localization-btn">
                                <Flag :lang="localization" />
                            </TabButton>
                        </div>
                    </div>
                    <div class="d-flex flex-column">
                        <FormButton color="primary" title="Demarer le workflow" @Click="nextReassociationWorkflow" class="mb-2" :disabled="cardsWorkflow.complete && reassociationWorkflow.complete">Demarer le workflow</FormButton>
                        <FormButton color="primary" title="Enregistrer les cartes" @click="saveCards" :disabled="!cardsWorkflow.complete || !reassociationWorkflow.complete" loading>Enregistrer</FormButton>
                    </div>
                </div>
            </div>
            <hr>
            <div class="container">
                <Row>
                    <Column size="sm">
                        <Checkbox v-model="hideValid" label="Masquer les cartes sans erreur" />
                        <Checkbox v-model="hideNotConflicts" label="N'afficher que les conflits entre cartes" />
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
            <div class="full-screen-container mt-2" v-for="cbs of cardsBySet" :key="cbs.set.id">
                <Collapse :open="false" class="set-collapse">
                    <template #label>
                        <PokemonSetLabel :set="cbs.set" showParent />
                    </template>
                    <template #after-label>
                        <Checkbox class="ms-1 mt-2" :modelValue="cbs.cards.every(e => isSelected(e))" @update:modelValue="v => selectAll(cbs.cards, v)" :slider="false" :indeterminate="cbs.cards.some(e => isSelected(e)) && cbs.cards.some(e => !isSelected(e))" />
                        <EditSetButton v-if="cbs.set?.id !== ''" :id="cbs.set.id" class="ms-2 round-btn-sm" />
                        <FormButton v-if="cbs.link" color="bulbapedia" :href="cbs.link" :newTab="true" class="ms-2 round-btn-sm">
                            <BulbapediaIcon />
                        </FormButton>
                    </template>
                    <ExtractedListTable :modelValue="cbs.cards" :mainLocalization="cbs.mainLocalization" @editCard="setEditCard" @removeCard="e => ignoreCard(e)" @resetCard="resetCard" @update:entry="setEntry" :groups="groups" @openReassociation="nextReassociationWorkflow" />
                </Collapse>
            </div>
        </template>
        <div v-else class="d-flex justify-content-center mt-2">
            <h4>Aucune carte en attente</h4>
        </div>
        <Modal label="Modifier la carte" size="xxl" v-bind="editCardModalProps">
            <template #label>
                <div class="d-flex flex-row">
                    Modifier la carte
                    <ExtractedCardStatus :entry="entries[cardsWorkflow.index]" class="ms-2" />
                </div>
            </template>
           <PokemonCardEditForm v-if="editCard" v-bind="editFormProps" />
        </Modal>
        <Modal ref="reassociationModal" label="Gestion des associations la carte" size="xxl" @close="reassociationWorkflow.reset()">
            <ReassociationForm v-model="reassociationGroup" @apply="applyReassociation" />
        </Modal>
    </Loading>
</template>

<script lang="ts" setup>
import {Flag, LocalizationCode, localizations, sortLocalizations} from '@/localization';
import rest from '@/rest';
import {downloadData, getDateStr} from '@/retriever';
import {ExtractedPokemonCardDTO, PokemonSetDTO} from '@/types';
import {
    ClearExtractedCards,
    ExtractedCardGroup,
    ExtractedCardStatus,
    ExtractedListTable,
    ExtractedPokemonCardEntry,
    extractedPokemonCardsService,
    legends,
    ReassociationForm,
    useCardGroups,
    useCardWorkflow,
    useExtractedPokemonCardEntries
} from '@components/cards/pokemon/extracted/list';
import Collapse from '@components/collapse/Collapse.vue';
import FormButton from '@components/form/FormButton.vue';
import Checkbox from '@components/form/Checkbox.vue';
import TabButton from '@components/tab/TabButton.vue';
import {ProgressBar} from '@/progress';
import Modal from '@components/modal/Modal.vue';
import {cloneDeep, isEmpty, isNumber, orderBy} from 'lodash';
import {computed, onMounted, ref, watch} from 'vue';
import Column from '@components/grid/Column.vue';
import Row from '@components/grid/Row.vue';
import {Legend, ScrollToTop, SideButtons} from '@components/side';
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";
import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {WorkflowComposables} from "@/vue/composables/WorkflowComposables";
import {ComposablesHelper} from "@/vue/composables/ComposablesHelper";
import Icon from "@components/Icon.vue";
import {EditSetButton, PokemonSetLabel} from "@components/cards/pokemon/set";
import {usePageTitle} from "@/vue/composables/PageComposables";
import Loading from "@components/Loading.vue";
import {useRaise} from "@/alert";
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";
import {useSelectedCards} from "@components/extraction";

class BySetEntry {
    readonly set: PokemonSetDTO;
    readonly cards: ExtractedPokemonCardEntry[];
    readonly link: string;
    private parent?: PokemonSetDTO;

    constructor(set: PokemonSetDTO) {
        this.set = set;
        this.cards = [];
        this.link = expansionsBulbapedia.value.find(e => e.setId === set.id)?.url || '';
        if (this.set.parentId) {
            PokemonComposables.pokemonSetService.get(this.set.parentId).then(p => this.parent = p);
        }
    }

    private findMainTranslation(s?: PokemonSetDTO) {
        if (!s) {
            return undefined;
        }
        return s.translations[mainLocalization.value] || Object.values(s.translations).find(t => t.localization === 'us' ||  t.localization === 'jp');
    }

    get mainTranslation() {
        return this.findMainTranslation(this.set);
    }

    get name() {
        return this.mainTranslation?.name;
    }

    get mainLocalization() {
        return this.mainTranslation?.localization || mainLocalization.value;
    }
}

usePageTitle("Pokémon - Ajout de cartes");

const raise = useRaise();

const sets = PokemonComposables.pokemonSetService.all;
const expansionsBulbapedia = PokemonComposables.useExpansionsBulbapedia();

const { entries, setEntry, ignoreCard, resetCard } = useExtractedPokemonCardEntries();
const {editCard, setEditCard, cardsWorkflow, editFormProps, editCardModalProps} = useCardWorkflow(entries, setEntry, ignoreCard);

const ready = ref(false);

watch([extractedPokemonCardsService.all, entries, sets], ([c, e, s]) => {
    ready.value = !isEmpty(s) && (isEmpty(c) || !isEmpty(e));
});
const hideValid = ref(false);
const hideNotConflicts = ref(false);
const availableLocalizations = computed<LocalizationCode[]>(() => sortLocalizations([...new Set(entries.value.flatMap(c => Object.values(c.extractedCard.card.translations)
    .filter(t => t?.available)
    .map(t => t.localization)
    .filter(l => !!l)))]));
const mainLocalization = ref<LocalizationCode>('us');

watch(availableLocalizations, l => {
    if (l.length > 0 && !l.includes(mainLocalization.value)) {
        mainLocalization.value = l[0];
    }
});

function isUnassigned(c: ExtractedPokemonCardEntry) {
    return isEmpty(c.extractedCard.card.setIds) || (c.extractedCard.card.translations?.[mainLocalization.value]?.available && !sets.value.some(s => s.translations?.[mainLocalization.value]?.available && c.extractedCard.card.setIds.includes(s.id)));
}

const cardsBySet = computed(() => {
    let usedSets: PokemonSetDTO[] = ([...new Set(entries.value
        .flatMap(c => c.extractedCard.card.setIds))]
        .map(id => sets.value.find(s => s.id === id)) as PokemonSetDTO[]); // TODO use pokemonSetService.get

    if (entries.value.some(c => isUnassigned(c))) {
        const set = PokemonSetService.newSet();

        set.translations[mainLocalization.value] = {
            localization: mainLocalization.value,
            name: 'Non assigné',
            available: false,
            originalName: ''
        };
        usedSets.unshift(set);
    }

    let value = orderBy(usedSets, s => s?.id === '' || !!s?.translations?.[mainLocalization.value]?.available, ['desc'])
        .filter(s => s !== undefined)
        .map(s => new BySetEntry(s));

    entries.value.filter(e => !!e.extractedCard.card?.translations?.[mainLocalization.value]?.available).forEach(c => {
        const unassigned = isUnassigned(c);

        for (let v of value) {
            if (unassigned ? (v.set?.id === '') : (c.extractedCard.card.setIds.includes(v.set?.id) && (!hideNotConflicts.value || c.status.includes('duplicate')) && (!c.status.includes('valid') || !hideValid.value))) {
                v.cards.push(c);
                break;
            }
        }
    });
    return value.filter(v => v.cards.length > 0);
});

const { selected, isSelected, revert, removeSelected, selectAll } = useSelectedCards(entries, e => e.id, ignoreCard);

const {save: saveCards} = SaveComposables.useLockedSaveAsync(async () => {
    if (!cardsWorkflow.value.complete) {
        raise.warn(`Il y a encore ${entries.value.length - cardsWorkflow.value.progress} cartes non valides.`);
        return;
    }

    let setIds: string[] = [];

    const doSaveCard = async (c: ExtractedPokemonCardDTO) => {
        if (isNumber(c.card)) {
            return;
        }

        let savedId = '';

        setIds.push(...c.card.setIds.filter(id => !setIds.includes(id)));
        if (c.savedCards.length) {
            savedId = c.savedCards[0].id;
            await rest.put(`/api/cards/pokemon/${c.savedCards.map(a => a.id).join(',')}/merge`, {data: c.card});
        } else {
            savedId = await rest.put('/api/cards/pokemon', {data: c.card});
        }

        await rest.put('/api/cards/pokemon/extracted/history', {data: c.rawExtractedCard});
        await Promise.all(extractedPokemonCardsService.all.value.map(async c2 => {
            if (c2.card.parentId === c.id) {
                c2.card.parentId = savedId;
                await doSaveCard(c2);
            }
        }));
    };

    try {
        await Promise.all(extractedPokemonCardsService.all.value.map(doSaveCard))
        await Promise.all(setIds.map(id => rest.post(`/api/cards/pokemon/sets/${id}/rebuild-ids-prim`)))
        await extractedPokemonCardsService.clear();

        entries.value = [];
        raise.success('Cartes enregistrées avec succès');
    } catch {
        raise.error('Une erreur est survenue lors de l\'enregistrement des cartes');
    }
});
const clearCards = async () => {
    entries.value = [];
}
const downloadJson = () => downloadData("extracted-pokemon-cards-" + getDateStr() + ".json", entries.value.map(c => c.extractedCard));
const {groups, setReviewed} = useCardGroups(entries);
const reassociationWorkflow = ComposablesHelper.unwrap(WorkflowComposables.useWorkflow(groups, { isValid: g => g?.reviewed }), 'next', 'isValid', 'reset');
const reassociationModal = ref<typeof Modal>();
const reassociationGroup = ref<ExtractedCardGroup>();
const nextReassociationWorkflow = (index?: number) => {
    const i = reassociationWorkflow.value.next(index);

    if (i !== undefined) {
        reassociationGroup.value = reassociationWorkflow.value.current;
        reassociationModal.value?.show();
    } else {
        reassociationGroup.value = undefined;
        reassociationModal.value?.hide();
        setEditCard();
    }
}
const applyReassociation = () => {
    reassociationGroup.value?.entries.forEach(e => {
        const entry = entries.value.find(c => c.extractedCard.id === e.extractedCard.id);

        if (entry) {
            const copy = cloneDeep(entry);

            copy.extractedCard.card = e.extractedCard.card;
            setEntry(copy);
        }
    });
    if (reassociationWorkflow.value.index !== undefined && groups.value[reassociationWorkflow.value.index]) {
        setReviewed(reassociationWorkflow.value.index);
    }
    nextReassociationWorkflow();
}
onMounted(() => extractedPokemonCardsService.fetch());
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
