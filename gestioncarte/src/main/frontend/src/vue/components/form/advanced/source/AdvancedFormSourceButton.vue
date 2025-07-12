<template>
    <template v-if="!readOnly && groupedSources?.length > 0">
        <FormButton color="info" class="form-btn ms-2" @click="open"><Icon name="list" /></FormButton>
        <Modal ref="modal" label="Consolidation" size="lg">
            <template v-if="isOpen">
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
                    <tr v-for="group in groupedSources" :key="group.value" :class="group.validationClass">
                        <td class="align-middle fw-bold">
                            <slot :value="group.value">{{ group.value }}</slot>
                            <Tooltip v-if="!!group.message">{{group.message}}</Tooltip>
                        </td>
                        <td class="align-middle">{{ group.weight }}</td>
                        <td>
                            <HoverDataList :value="group.sources" #default="{value: v}">
                                <a v-if="v.link" :href="v.link" target="_blank" rel="noopener"> {{ v.source }}</a>
                                <span v-else>{{ v.source }}</span>
                            </HoverDataList>
                        </td>
                        <td class="align-middle justify-content-end text-end">
                            <MergeButton side="left" @click="update(group.value)" />
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
        </Modal>
    </template>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue';
import {Path, pathToString} from "@/path";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import {useConsolidationSources} from "@components/form/advanced/source/logic";
import FormButton from "@components/form/FormButton.vue";
import Modal from "@components/modal/Modal.vue";
import Icon from "@components/Icon.vue";
import MergeButton from "@components/form/MergeButton.vue";
import Column from "@components/grid/Column.vue";
import Row from "@components/grid/Row.vue";
import Tooltip from "@components/tooltip/Tooltip.vue";
import HoverDataList from "@components/table/HoverDataList.vue";
import {
    getStrongestValidationStatus,
    getValidationClass,
    getValidationMessage,
    validate as doValidate
} from "@/validation";
import {computedAsync} from "@vueuse/core";
import {chain, get, isEmpty, isNil, sum} from "lodash";

interface Props {
    path: Path;
}

const props = defineProps<Props>();

const { value, rules, readOnly } = useAdvancedFormInput<any, any>(() => props.path);
const { sources, names } = useConsolidationSources();

const validate = async (v: any) => (await doValidate<any>(rules.value, v))[pathToString(props.path)];

const groupedSources = computedAsync(async () => {
    const resolved = await Promise.all(chain(sources.value)
        .groupBy(t => {
            const v = get(t.value, props.path);

            return isNil(v) ? "" : v.toString();
        })
        .mapValues(async (v, k) => {
            const status = getStrongestValidationStatus(await validate(k));

            return {
                value: k,
                message: getValidationMessage(status),
                sources: v.map(t => {
                    return {
                        source: t.name ?? "",
                        link: t.link ?? ""
                    }
                }),
                validationClass: getValidationClass(status),
                weight: sum(v.map(t => t.weight ?? 1)),
                blocking: v.some(t => t.blocking ?? true)
            };
        }).values().flatten().value());

    return chain(resolved)
        .filter(g => !isNil(g.value) && !isEmpty(g.value))
        .orderBy(v => v.weight, 'desc')
        .value();
});
const missingSources = computed(() => names.value.filter(n => !sources.value.some(s => s.name === n)));

const update = (v: any) => {
    value.value = v;
    modal.value?.hide()
};


const modal = ref<typeof Modal>();
const open = () => modal.value?.show();
const isOpen = computed(() => modal.value?.visible);
</script>
