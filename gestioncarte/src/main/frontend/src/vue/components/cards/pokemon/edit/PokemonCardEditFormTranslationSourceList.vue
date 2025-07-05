<template>
    <table class="table table-striped" v-bind="$attrs">
        <thead>
            <tr>
                <th scope="col">Valeur</th>
                <th scope="col">Poid</th>
                <th scope="col">Sources</th>
                <th scope="col"></th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="group in groupedSourceTranslations" :key="group.value" :class="group.validationClass">
                <td class="align-middle fw-bold">
                    {{ group.value }}
                    <Tooltip v-if="!!group.message">{{group.message}}</Tooltip>
                </td>
                <td class="align-middle">{{ group.weight }}</td>
                <td>
                    <HoverDataList :value="group.sources" v-slot="{value: v}">
                        <a v-if="v.link" :href="v.link" target="_blank" rel="noopener"> {{ v.source }}</a>
                        <span v-else>{{ v.source }}</span>
                    </HoverDataList>
                </td>
                <td class="align-middle justify-content-end text-end">
                    <MergeButton side="left" @click="$emit('update:value', group.value)" />
                </td>
            </tr>
        </tbody>
    </table>
    <Row v-if="missingSources.length > 0">
        <Column>
            <label class="form-label ps-0">Source Manquantes: </label>
            <span v-for="s in missingSources" :key="s" class="badge rounded-pill ms-2 missing-source bg-danger">{{s}}</span>
        </Column>
    </Row>
</template>

<script lang="ts" setup>
import {PokemonCardDTO, PokemonCardTranslationDTO, SourcedPokemonCardTranslationDTO} from '@/types';
import {computed} from 'vue';
import MergeButton from '@components/form/MergeButton.vue';
import {chain, isEmpty, isNil, sum, toPath} from "lodash";
import HoverDataList from "@components/table/HoverDataList.vue";
import Tooltip from "@components/tooltip/Tooltip.vue"
import Column from "@components/grid/Column.vue";
import Row from "@components/grid/Row.vue";
import {getValidationClass, getValidationMessage} from "@/validation";
import {computedAsync} from "@vueuse/core";
import {Path} from "@/path";
import {LocalizationCode} from "@/localization";
import {pokemonCardTranslatorService} from "@components/cards/pokemon/translators";
import {useValidateTranslation} from "@components/cards/pokemon/edit/validation";
import {EditedPokemonCard} from "@components/cards/pokemon/edit/logic";

interface Props {
    path: Path;
    localization: LocalizationCode;
    sourceTranslations: SourcedPokemonCardTranslationDTO[];
    card: PokemonCardDTO | EditedPokemonCard;
}
interface Emits {
    (e: 'update:value', value: any): void;
}

const props = defineProps<Props>();
defineEmits<Emits>();

const validate = useValidateTranslation(() => props.card);

type TargetKey = keyof PokemonCardTranslationDTO | undefined;
const target = computed<TargetKey>(() => {
    const split = toPath(props.path);

    return (split.length > 0 ? split[split.length - 1] : props.path) as TargetKey;
});

const computedSourceTranslations = computed(() => props.sourceTranslations.filter(t => t.localization === props.localization));

const groupedSourceTranslations = computedAsync(async () => {
    if (!target.value) {
        return [];
    }

    const resolved = await Promise.all(chain(computedSourceTranslations.value)
        .groupBy(t => t.translation[target.value as keyof PokemonCardTranslationDTO])
        .mapValues(async (v, k) => {
            const tr = (await Promise.all(v.map(t => pokemonCardTranslatorService.find(translator => translator.code === t.source)))).flat();
            const status = await validate(k, props.path);

            return {
                value: k,
                message: getValidationMessage(status),
                sources: v.map(t => {
                    return {
                        source: t.source,
                        link: t.link,
                    }
                }),
                validationClass: getValidationClass(status),
                weight: sum(tr.map(t => t.weight ?? 1)),
                blocking: tr.some(t => t.blocking)
            };
        }).values().flatten().value());

    return chain(resolved)
        .filter(g => !isNil(g.value) && !isEmpty(g.value))
        .orderBy(v => v.weight, 'desc')
        .value();
});

const missingSources = computed(() => pokemonCardTranslatorService.all.value
    .filter(t => t.localizations.includes(props.localization) && t.blocking && !computedSourceTranslations.value.find(p => p.source === t.code) && (!t.code.endsWith('-to-jp') || target.value !== 'originalName'))
    .map(t => t.code));
</script>

<style lang="scss" scoped>
@import "src/vue/components/form/advanced/AdvancedForm.scss";

@include for-each-status() using ($status, $color) {
    tr.is-#{$status} {
        background-color: rgba($color, 0.5);
    }
}
</style>
