<template>
	<div class="container mt-3" v-if="modelValue">
		<Row class="form-group">
			<CardCollapse label="Traductions" :open="show">
                <template v-slot:header>
                    <div class="float-end mb-2">
                        <FormButton v-for="localization in missingLocalizations" :key="localization" color="link" class="p-0 no-focus me-1" @click="addLocalization(localization)">
                            <Flag :lang="localization" />
                        </FormButton>
                    </div>
                </template>
				<template v-for="localization in localizationDescriptions" :key="localization.code">
					<CardCollapse open class="mb-3 border-2" @toggle="o => $emit('toggleCollapse', localization.code, o)">
						<template v-slot:label>
							<Flag :lang="localization.code" class="icon-16 me-2"></Flag>{{localization.name}}
						</template>
						<template v-slot:header v-if="removable">
							<FormButton color="link" class="no-focus float-end pe-0" @click="remove(localization.code)">
                                <Icon class="text-danger icon-24" name="trash"/>
                            </FormButton>
						</template>
						<template v-slot:default>
							<slot name="content" :localization="localization" :item="modelValue[localization.code]" :disabled="disabled">
								<FormRow>
									<FormInput :modelValue="modelValue[localization.code]?.name" @update:modelValue="v => setName(localization.code, v)" :disabled="disabled" :required="isRequired(localization.code)">
                                        <template v-slot:label>
                                            <span class="fw-bold">Nom</span>
                                        </template>
                                    </FormInput>
                                    <slot name="name-after" :localization="localization" :item="modelValue[localization.code]" :disabled="disabled" :required="isRequired(localization.code)"></slot>
									<FormInput v-if="useOriginalName && localization.hasOriginalName" label="Nom en caractère kanji (日本語)" :modelValue="modelValue[localization.code]?.originalName" @update:modelValue="v => setOriginalName(localization.code, v)" :disabled="disabled" :required="false"></FormInput>
								</FormRow>
							</slot>
							<slot :localization="localization" :item="modelValue[localization.code]" :disabled="disabled" :required="isRequired(localization.code)"></slot>
						</template>
					</CardCollapse>
				</template>
			</CardCollapse>
		</Row>
	</div>
</template>

<script lang="ts" setup>
import {Flag, getLocalization, LocalizationCode, localizationCodes, Translations} from '@/localization';
import {map, remove as lodashRemove} from 'lodash';
import {computed, onMounted, watch} from 'vue';
import CardCollapse from '@components/collapse/CardCollapse.vue';
import FormButton from '@components/form/FormButton.vue';
import FormInput from '@components/form/FormInput.vue';
import FormRow from '@components/form/FormRow.vue';
import Row from '@components/grid/Row.vue';
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import Icon from "@components/Icon.vue";

interface Translation {
	name: string;
	originalName?: string;
	available?: boolean;
	localization: LocalizationCode;
}

type FormTranslations = Translations<Translation>;

const getOrCreate = (value: FormTranslations, localization: LocalizationCode) => {
	let v = value[localization];

	if (!v) {
		v = { name: "", available: true, localization: localization };
		value[localization] = v;
	} else if (!v.available) {
        v.available = true;
    }
	return v;
}

interface Props {
	localizations?: LocalizationCode[];
	removable?: boolean;
	disabled?: boolean;
	show?: boolean;
	useOriginalName?: boolean;
	modelValue?: FormTranslations;
	requiredLocalizations?: LocalizationCode[];
}
interface Emits {
	(e: 'update:modelValue', value: FormTranslations): void;
	(e: 'update:localizations', value: LocalizationCode[]): void;
    (e: 'toggleCollapse', localization: LocalizationCode, open: boolean): void;
}

const props = withDefaults(defineProps<Props>(), {
    modelValue: () => ({}),
	localizations: () => ['us', 'jp'],
	removable: false,
	disabled: false,
	show: false,
	useOriginalName: true,
	requiredLocalizations: () => ['us', 'jp']
});
const emit = defineEmits<Emits>();

const update = ModelComposables.useUpdateModel<FormTranslations>(() => props.modelValue, emit);
const updateLocalizations = ModelComposables.useUpdateModel<LocalizationCode[], 'update:localizations'>(() => props.localizations, emit, 'update:localizations');
const isRequired = (localization: LocalizationCode) => !!(props.requiredLocalizations.includes(localization) || props.modelValue[localization]?.name);
const remove = (localization: LocalizationCode) => {
    update(v => getOrCreate(v, localization).available = false);
    updateLocalizations((v: LocalizationCode[]) => lodashRemove(v, l => l === localization));
}
const setName = (localization: LocalizationCode, name: string) => update(v => getOrCreate(v, localization).name = name);
const setOriginalName = (localization: LocalizationCode, name: string) => update(v => getOrCreate(v, localization).originalName = name);
const localizationDescriptions = computed(() => map(props.localizations, (l: LocalizationCode) => getLocalization(l)));
const populate = () => {
	const keys = Object.keys(props.modelValue);

	if (!props.localizations.every(l => keys.includes(l))) {
		update(v => props.localizations.forEach(l => getOrCreate(v, l)))
	}
};
const addLocalization = (loc: LocalizationCode) => updateLocalizations(locs => locs.push(loc));
const missingLocalizations = computed(() => localizationCodes.filter(l => !props.localizations.includes(l)));

watch([() => props.localizations, () => props.modelValue], populate);
onMounted(populate);
</script>
