import {FieldMappingDTO, Service} from "@/types";

export const fieldMappingService = new Service<FieldMappingDTO, string>('/api/fields/mappings');
