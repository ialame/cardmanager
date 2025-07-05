import {Consumer, PokemonCardDTO} from "@/types";
import {MaybeRefOrGetter, toRef} from "@vueuse/core";
import {cloneDeep, uniq} from "lodash";
import {computed, ref, Ref} from "vue";
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";
import Modal from "@components/modal/Modal.vue";
import {ComposablesHelper} from "@/vue/composables/ComposablesHelper";
import {WorkflowComposables} from "@/vue/composables/WorkflowComposables";
import {Path} from "@/path";
import {ExtractionStatusLegendType} from "@components/extraction";
import {ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";

export type NameKey  = 'name' | 'labelName' | 'originalName';

export const legends: ExtractionStatusLegendType[] = [
    { iconName: "mail-unread-outline", color: "success", text: "Nouvelle carte", key: 'new' },
    { iconName: "git-merge", text: "Mariage automatique", key: 'merge' },
    { iconName: "git-merge", color: "danger", text: "Mariage conflictuel", key: 'breaking-merge' },
    { iconSrc: "/svg/db.svg", color: "warning", text: "Carte avec modification", key: 'change' },
    { iconSrc: "/svg/db.svg", color: "danger", text: "Carte avec modification bloquante", key: 'breaking-change' },
    // TODO { iconSrc: "/svg/bracket.svg", color: "danger", text: "Carte avec des changements dans les crochets", key: 'bracket-change' },
    { iconName: "file-tray-outline", color: "danger", text: "Non traduit dans toutes les langues", key: 'missing-translation' },
    { iconName: "warning-outline", color: "danger", text: "Conflit entre cartes", key: 'duplicate' },
    { iconName: "language-outline", color: "danger", text: "Conflit de traduction", key: 'translation-conflict' },
    { iconName: "language-outline", color: "success", text: "Nouveau charset", key: 'new-charset' },
    { iconSrc: "/svg/404.svg", color: "warning", text: "Page 2 non trouvée", key: 'bulbapedia-no-page-2' },
    { iconSrc: "/svg/bulbapedia-p2.svg", color: "warning", text: "Carte non trouvée en page 2", key: 'bulbapedia-not-in-page-2' },
    { iconSrc: "/svg/bulbapedia-p1.svg", color: "warning", text: "Association non trouvée en page 1", key: 'bulbapedia-not-in-page-1' },
    { iconName: "albums-outline", color: "danger", text: "Pas d'extension Bulbapedia pour cette association", key: 'bulbapedia-no-set' },
    { iconName: "close-outline", color: "danger", text: "Carte à vérifier", key: 'invalid' },
    { iconName: "checkmark-outline", color: "success", text: "Carte vérifiée", key: 'reviewed' },
];

export function useCardWorkflow(entries: MaybeRefOrGetter<ExtractedPokemonCardEntry[]>, setEntry: Consumer<ExtractedPokemonCardEntry>, ignoreCard: Consumer<ExtractedPokemonCardEntry>) {
    const editForm = ref<typeof PokemonCardEditForm>() as Ref;
    const editCardModal = ref<typeof Modal>() as Ref;
    const cardsWorkflow = ComposablesHelper.unwrap(WorkflowComposables.useWorkflow(entries, { isValid: c => !c.status.includes('invalid') }), 'next', 'isValid', 'reset');
    const editCard = ref<PokemonCardDTO>();
    const optionalPaths = ref<Path[]>([]);
    const reviewedPaths = ref<Path[]>([]);
    const defaultSavedCardIds = ref<string[]>([]);
    const entriesRef = toRef(entries);

    const setEditCard = (entry?: ExtractedPokemonCardEntry) => {
        const index = entry ? entriesRef.value.findIndex(e => e.extractedCard.id === entry?.extractedCard.id) : undefined;
        const i = cardsWorkflow.value.next(index);

        if (i !== undefined && !entry) {
            entry = entriesRef.value[i];
        }
        if (i !== undefined && entry) {
            editCard.value = cloneDeep(entry.extractedCard.card);
            optionalPaths.value = cloneDeep(entry.optionalPaths);
            reviewedPaths.value = cloneDeep(entry.reviewedPaths);
            defaultSavedCardIds.value = entry.extractedCard.savedCards.map(c => c.id);
            editForm.value?.reset?.();
            editCardModal.value?.show?.();
        } else {
            editCard.value = undefined;
            editCardModal.value?.hide?.();
        }
    };
    const save = async (savedCards: PokemonCardDTO[]) => {
        if (cardsWorkflow.value.index !== undefined && editCard.value) {
            const entry = cloneDeep(entriesRef.value[cardsWorkflow.value.index]);

            entry.extractedCard.card = cloneDeep(editCard.value);
            entry.extractedCard.savedCards = uniq(savedCards);
            entry.extractedCard.reviewed = true;
            entry.reviewedPaths = cloneDeep(reviewedPaths.value);
            entry.optionalPaths = cloneDeep(optionalPaths.value);
            entry.status = [];
            entry.validationResults = {};
            setEntry(entry);
            setEditCard();
        }
    }
    const ignore = () => {
        if (cardsWorkflow.value.index) {
            ignoreCard(entriesRef.value[cardsWorkflow.value.index]);
            cardsWorkflow.value.reset();
            setEditCard();
        }
    }

    const editFormProps = computed(() => ({
        ref: editForm,
        modelValue: editCard.value,
        optionalPaths: optionalPaths.value,
        reviewedPaths: reviewedPaths.value,
        defaultSavedCardIds: defaultSavedCardIds.value,
        'onUpdate:modelValue': (v: PokemonCardDTO) => editCard.value = v,
        'onUpdate:optionalPaths': (v: Path[]) => optionalPaths.value = v,
        'onUpdate:reviewedPaths': (v: Path[]) => reviewedPaths.value = v,
        'onSave': save,
        'onIgnore': ignore,
        showIgnore: true,
        showFullArt: false,
    }));
    const editCardModalProps = computed(() => ({
        ref: editCardModal,
        'onClose': () => cardsWorkflow.value.reset(),
    }));

    return {editCard, setEditCard, cardsWorkflow, editFormProps, editCardModalProps};
}
