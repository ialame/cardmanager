<template>
	<div class="container set-search">
		<FormRow v-if="firstRow && !disabled">
			<FormInput label="Recherche par id" v-model="searchId" @submit="$emit('submit')"></FormInput>
			<Column  size="sm" class="form-group">
				<label class="form-label">Filtrer par langue</label>
                <LocalizationSelect v-model="localizationFilter" @update:model-value="$emit('update:localizationFilter', $event)" :localizations="['us','fr','it','es','de','pt', 'jp']" :showAll="true" />
			</Column>
			<Column size="sm" class="form-group">
				<label class="form-label">Filtrer par promo</label>
				<select v-model="promoFilter" class="form-select form-control" aria-label="Filtrer par promo">
					<option value="all" selected>Toutes</option>
					<option value="false">Non Promo</option>
					<option value="true">Promo</option>
				</select>
			</Column>
		</FormRow>
		<FormRow>
			<Column v-if="displaySeries?.length > 1" size="4" class="form-group">
                <label class="form-label">Serie</label>
                <PokemonSerieSelect v-model="serieFilter" :langue="localizationFilter" :values="displaySeries" :disabled="disableSeries" hasAllOption />
			</Column>
			<Column :size="displaySeries?.length > 1 ? '8' : ''" class="form-group">
				<label class="form-label">Extension</label>
				<div class="d-flex flex-row">
                    <PokemonSetSelect class="set-dropdown h-100" v-model="selected" :langue="localizationFilter" :values="displaySets" :disabled="disableSets" />
					<div class="ms-2" v-if="canEditSet">
                        <EditSetButton :id="selected?.id" />
					</div>
					<slot name="buttons" />
				</div>
			</Column>
		</FormRow>
		<slot />
	</div>
</template>

<script lang="ts" setup>
import {PokemonSerieDTO, PokemonSetDTO} from '@/types';
import FormInput from '@components/form/FormInput.vue';
import FormRow from '@components/form/FormRow.vue';
import Column from '@components/grid/Column.vue';
import {isEmpty} from 'lodash';
import {computed, ref, watch, watchEffect} from 'vue';
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import {PokemonSerieSelect, pokemonSerieService} from "@components/cards/pokemon/serie";
import {useVModel} from "@vueuse/core";
import {useDisplaySeries, useDisplaySets} from "./logic";
import {LocalizationCode} from "@/localization";
import EditSetButton from "@components/cards/pokemon/set/EditSetButton.vue";
import PokemonSetSelect from "@components/cards/pokemon/set/PokemonSetSelect.vue";
function placeKeyFirst<T extends object>(obj: T, keyToFirst: keyof T): Record<string, any> {
  const { [keyToFirst]: valueToFirst, ...rest } = obj;

  // On reconstruit un objet avec la clé désirée en premier
  return { [keyToFirst]: valueToFirst, ...rest };
}

type FilterValue = LocalizationCode ;// | 'all';
type PromoFilterValue = 'all' | 'true' | 'false';

interface Props {
    series?: PokemonSerieDTO[];
    sets?: PokemonSetDTO[];
	modelValue?: PokemonSetDTO;
	canEditSet?: boolean;
	firstRow?: boolean;
	required?: boolean;
	localization?: FilterValue;
	disabled?: boolean;
}

// ---- INTERFACE DES ÉVÉNEMENTS ----
interface Emits {
	(e: 'update:modelValue', value: PokemonSetDTO): void;
  (e: 'update:localizationFilter', value: LocalizationCode): void;
	(e: 'submit'): void;
}

const props = withDefaults(defineProps<Props>(), {
    series: () => [],
	sets: () => [],
	canEditSet: true,
	firstRow: true,
	required: false,
	localization: 'us' // 'all'
});

const emit = defineEmits<Emits>();

const selected = useVModel(props, 'modelValue', emit);

const searchId = ref("");
const localizationFilter = ref(props.localization);
const serieFilter = ref<PokemonSerieDTO>();
const promoFilter = ref<PromoFilterValue>("all");

const isPromoUs = computed<boolean>(() => localizationFilter.value === 'us' && promoFilter.value === 'true');
const displaySeries = useDisplaySeries(s => !isPromoUs.value && ( s?.translations?.[localizationFilter.value] !== undefined), () => props.series);
const displaySets = useDisplaySets(s => (isEmpty(searchId.value) || s.id.toString().startsWith(searchId.value))
    && ( s?.translations?.[localizationFilter.value]?.available || false)
    && (!serieFilter.value || serieFilter.value?.id === s.serieId)
    && (promoFilter.value === 'all' || promoFilter.value === s.promo?.toString()), () => props.sets);

const disableSeries = computed(() => props.disabled || isEmpty(displaySeries.value) || isPromoUs.value);
const disableSets = computed(() => props.disabled || isEmpty(displaySets.value));

watchEffect(async () => {
    if (serieFilter.value && !props.disabled) {
        return;
    }
    if (props.modelValue?.serieId) {
        serieFilter.value = await pokemonSerieService.get(props.modelValue.serieId);
    } else if (displaySeries.value?.length === 1) {
        serieFilter.value = displaySeries.value[0];
    }
})
watch(() => props.localization, l => localizationFilter.value = l);
watch(displaySets, value => {
    if (searchId.value && !isEmpty(value)) {
        selected.value = value[0];
    }
});

</script>

<style lang="scss" scoped>

@import 'src/variables';
@import 'src/mixins';

@mixin form-validation-state-selector($state) {
    @if ($state == "valid" or $state == "invalid") {
        .was-validated #{if(&, "&", "")}:#{$state},
        #{if(&, "&", "")}.set-select {
            @content;
        }
    } @else {
        #{if(&, "&", "")}.set-select {
            @content;
        }
    }
}

.container.set-search {
    &.new-highlight { box-shadow: none; }
    &.diff-highlight { box-shadow: none; }
    &.merge-highlight { box-shadow: none; }
    &.invalid-shadow { box-shadow: none; }

    &.new-highlight:deep(.set-select:not([disabled])) { @include highlight($green); }
    &.diff-highlight:deep(.set-select:not([disabled])) { @include highlight($yellow); }
    &.merge-highlight:deep(.set-select:not([disabled])) { @include highlight($info); }
    &.invalid-shadow:deep(.set-select:not([disabled])) { @include highlight($danger); }

    &.is-invalid:deep {
        @include form-validation-state('invalid', $form-feedback-invalid-color, $form-feedback-icon-invalid);
    }
}

:deep(.set-dropdown) {
    min-width: 0;
    width: 100%;
}
</style>
