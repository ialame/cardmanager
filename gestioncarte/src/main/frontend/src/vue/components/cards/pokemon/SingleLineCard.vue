<template>
    <Row class="m-2 c-pointer border border-primary rounded single-line-card" v-if="card && card.translations[computedLocalization]">
        <slot />
		<Column>
			<Row>
				<Column size="1" class="align-items-center w-auto">
					<span>{{card.translations[computedLocalization]?.name}} </span>
                    <span v-if="card.level" class="small"> Lv.{{card.level}}</span>
					<span class="fw-bold" v-if="brackets">{{brackets}}</span>
                    <IdTooltip :id="card.id" />
				</Column>
				<Column size="1" class="align-items-center w-auto">
                    <FormButton v-for="l in localizations.filter(loc => flagFilter.includes(loc))" :key="l" color="link" @click="selectedLocalization = l" class="p-0 no-focus">
                        <Flag :lang="l" class="me-1" />
                    </FormButton>
				</Column>
				<Column v-if="promos.length" size="lg">
					<HoverDataList #default="{ value: promo }" :value="promos">
						<span class="small">{{promo}}</span>
                    </HoverDataList>
				</Column>
			</Row>
			<Row>
				<Column>{{card.translations[computedLocalization]?.number}}</Column>
			</Row>
			<Row v-if="computedSet !== undefined">
				<Column>
                    {{ computedSet?.translations?.[computedLocalization]?.name }}
                    <IdTooltip :id="computedSet.id" />
                </Column>
			</Row>
		</Column>
        <Column size="1" class="d-flex align-items-center justify-content-end w-auto">
            <slot name="buttons">
				<a :href="'/cards/pokemon/' + card.id" class="btn btn-info" target="_blank"><Icon name="information-circle-outline" class="icon-24" /></a>
			</slot>
        </Column>
	</Row>

</template>

<script lang="ts" setup>
import {Flag, LocalizationCode, localizationCodes} from "@/localization";
import {PokemonCardDTO} from "@/types";
import {chain, find, isEmpty} from "lodash";
import {computed, ref} from "vue";
import Column from "@components/grid/Column.vue";
import Row from "@components/grid/Row.vue";
import HoverDataList from "@components/table/HoverDataList.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";

interface Props {
    card: PokemonCardDTO;
    localization?: LocalizationCode;
    flagFilter?: LocalizationCode[];
}

const props = withDefaults(defineProps<Props>(), {
    flagFilter: () => localizationCodes
});
const sets = PokemonComposables.pokemonSetService.all;

const selectedLocalization = ref<LocalizationCode>();

const brackets = computed(() => {
    const bracketNames = chain(props.card.brackets)
        .filter(b => b.localization === computedLocalization.value)
        .map('name')
        .join(', ')
        .value();

    return !isEmpty(bracketNames) ? (' [' + bracketNames + ']') : '';
});
const computedSet = computed(() => chain(props.card.setIds)
    .map(id => find(sets.value, s => s.id == id))
    .filter(s => !!s?.translations?.[computedLocalization.value])
	.head()
    .value());
const promos = computed(() => chain(props.card.promos)
    .filter(p => p.localization === computedLocalization.value)
    .map("name")
    .value());
const localizations = computed(() => Object.values(props.card.translations)
	.filter(t => t.available)
	.map(t => t.localization) as LocalizationCode[]);
const computedLocalization = computed<LocalizationCode>(() => {
    if (selectedLocalization.value && localizations.value.includes(selectedLocalization.value)) {
        return selectedLocalization.value;
    }
    if (props.localization && localizations.value.includes(props.localization)) {
        return props.localization;
    }
    return chain(localizationCodes)
        .filter(l => computedSet.value?.translations?.[l] != undefined)
        .head()
        .value() as LocalizationCode;
});
</script>

<style lang="scss" scoped>
@import "src/variables";

.single-line-card {
    background-color: $light-bg;
}
</style>
